package me.wolfyscript.customcrafting.network;

import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.network.messages.MessageAPI;
import me.wolfyscript.utilities.api.nms.NetworkUtil;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class NetworkHandler {

    private static final String KEY = NamespacedKeyUtils.NAMESPACE;
    private static final NamespacedKey DATA_REQUEST = new NamespacedKey(KEY, "data_request");
    private static final NamespacedKey RECIPE_LIST_SIZE = new NamespacedKey(KEY, "recipe_list_size"); //Sends size of recipes the client will receive.
    private static final NamespacedKey RECIPE_LIST = new NamespacedKey(KEY, "recipe_list"); //Sends the recipe data.
    private static final NamespacedKey RECIPE_LIST_END = new NamespacedKey(KEY, "recipe_list_end"); //Sent when all recipes are transmitted.
    private static final NamespacedKey RECIPE_BOOK_SETTINGS = new NamespacedKey(KEY, "recipe_book_settings");
    private final WolfyUtilities wolfyUtilities;
    private final MessageAPI api;
    private final NetworkUtil networkUtil;

    public NetworkHandler(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.api = this.wolfyUtilities.getMessageAPI();
        this.networkUtil = this.wolfyUtilities.getNmsUtil().getNetworkUtil();
    }

    public void registerPackets() {
        api.register(DATA_REQUEST, (player, wolfyUtilities1, mcByteBuf) -> {
            //Decode request and verify!
            if (wolfyUtilities.getPermissions().hasPermission(player, "customcrafting.network.receive_data")) {
                Bukkit.getScheduler().runTaskLater(wolfyUtilities.getPlugin(), () -> sendRecipes(player), 3);
            }
        });

        //Register outgoing packets
        api.register(RECIPE_LIST_SIZE);
        api.register(RECIPE_LIST);
        api.register(RECIPE_LIST_END);
        api.register(RECIPE_BOOK_SETTINGS);
    }

    public void sendRecipes(Player player) {
        List<ICustomRecipe<?, ?>> recipes = Registry.RECIPES.getAvailable(player);

        //Send size of recipe list! Client will wait for recipe packets after it receives this packet.
        MCByteBuf mcByteBuf = networkUtil.buffer();
        mcByteBuf.writeVarInt(recipes.size());
        api.send(RECIPE_LIST_SIZE, player, mcByteBuf);

        //Send recipes and compact multiple into one packet
        int recipesLeft = recipes.size();
        MCByteBuf recipeBuf = null;
        int recipesPerPacket = 0;
        int i = 0;
        for (ICustomRecipe<?, ?> recipe : recipes) {
            if (recipeBuf == null) {
                recipesPerPacket = Math.min(recipesLeft, 4); //Replace recipes per packet with config option
                recipeBuf = networkUtil.buffer();
                recipeBuf.writeVarInt(recipesPerPacket);
            }
            if (i < recipesPerPacket) {
                recipe.writeToBuf(recipeBuf);
                i++;
                recipesLeft--;
            } else {
                api.send(RECIPE_LIST, player, recipeBuf);
                recipeBuf = null;
                recipesPerPacket = 0;
                i = 0;
            }
        }

        //Send end packet
        api.send(RECIPE_LIST_END, player);
    }
}
