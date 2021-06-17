package me.wolfyscript.customcrafting.network;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
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
import java.util.Map;

public class NetworkHandler {

    private static final String KEY = NamespacedKeyUtils.NAMESPACE;
    private static final NamespacedKey DATA_REQUEST = new NamespacedKey(KEY, "data_request");
    private static final NamespacedKey RECIPE_LIST_SIZE = new NamespacedKey(KEY, "recipe_list_size"); //Sends size of recipes the client will receive.
    private static final NamespacedKey RECIPE_LIST = new NamespacedKey(KEY, "recipe_list"); //Sends the recipe data.
    private static final NamespacedKey RECIPE_LIST_END = new NamespacedKey(KEY, "recipe_list_end"); //Sent when all recipes are transmitted.

    private static final NamespacedKey RECIPE_BOOK_SETTINGS = new NamespacedKey(KEY, "recipe_book_settings");
    private static final NamespacedKey RECIPE_BOOK_SETTINGS_SIZE = new NamespacedKey(KEY, "recipe_book_settings_size");
    private static final NamespacedKey RECIPE_BOOK_CATEGORIES = new NamespacedKey(KEY, "recipe_book_categories");
    private static final NamespacedKey RECIPE_BOOK_FILTERS = new NamespacedKey(KEY, "recipe_book_filters");
    private static final NamespacedKey RECIPE_BOOK_FILTERS_END = new NamespacedKey(KEY, "recipe_book_filters_end");

    private final CustomCrafting customCrafting;
    private final WolfyUtilities wolfyUtilities;
    private final MessageAPI api;
    private final NetworkUtil networkUtil;

    public NetworkHandler(CustomCrafting customCrafting, WolfyUtilities wolfyUtilities) {
        this.customCrafting = customCrafting;
        this.wolfyUtilities = wolfyUtilities;
        this.api = this.wolfyUtilities.getMessageAPI();
        this.networkUtil = this.wolfyUtilities.getNmsUtil().getNetworkUtil();
    }

    public void registerPackets() {
        api.register(DATA_REQUEST, (player, wolfyUtilities1, mcByteBuf) -> {
            //Decode request and verify!
            if (wolfyUtilities.getPermissions().hasPermission(player, "customcrafting.network.receive_data")) {
                Bukkit.getScheduler().runTaskLater(wolfyUtilities.getPlugin(), () -> {
                    sendRecipes(player);
                    sendRecipeBookSettings(player);
                }, 3);
            }
        });

        //Register outgoing packets
        api.register(RECIPE_LIST_SIZE);
        api.register(RECIPE_LIST);
        api.register(RECIPE_LIST_END);
        api.register(RECIPE_BOOK_SETTINGS);
        api.register(RECIPE_BOOK_CATEGORIES);
        api.register(RECIPE_BOOK_FILTERS);
        api.register(RECIPE_BOOK_FILTERS_END);
    }

    public void sendRecipes(Player player) {
        List<ICustomRecipe<?, ?>> recipes = Registry.RECIPES.getAvailable(player);

        //Send size of recipe list! Client will wait for recipe packets after it receives this packet.
        MCByteBuf mcByteBuf = networkUtil.buffer();
        mcByteBuf.writeVarInt(recipes.size());
        api.send(RECIPE_LIST_SIZE, player, mcByteBuf);

        //Send recipes
        for (ICustomRecipe<?, ?> recipe : recipes) {
            var recipeBuf = networkUtil.buffer();
            recipe.writeToBuf(recipeBuf);
            api.send(RECIPE_LIST, player, recipeBuf);
        }

        //Send end packet
        api.send(RECIPE_LIST_END, player);
    }

    public void sendRecipeBookSettings(Player player) {
        var categories = customCrafting.getConfigHandler().getRecipeBookConfig().getCategories();
        Map<String, CategoryFilter> filtersMap = categories.getFilters();
        Map<String, Category> categoryMap = categories.getCategories();

        MCByteBuf settingsBuf = networkUtil.buffer();
        settingsBuf.writeVarInt(categoryMap.size());
        writeNamespacedKeyList(categories.getSortedCategories(), settingsBuf);
        settingsBuf.writeVarInt(filtersMap.size());
        writeNamespacedKeyList(categories.getSortedFilters(), settingsBuf);
        api.send(RECIPE_BOOK_SETTINGS, player, settingsBuf);

        MCByteBuf categoriesBuf = networkUtil.buffer();
        categoriesBuf.writeVarInt(categoryMap.size());
        categoryMap.forEach((key, category) -> {
            categoriesBuf.writeUtf(NamespacedKeyUtils.NAMESPACE + ":" + key);
            category.writeToByteBuf(categoriesBuf);
        });
        api.send(RECIPE_BOOK_CATEGORIES, player, categoriesBuf);

        //Sending recipe book filters (split them into single packets!)
        filtersMap.forEach((key, filter) -> {
            MCByteBuf filtersBuf = networkUtil.buffer();
            filtersBuf.writeUtf(NamespacedKeyUtils.NAMESPACE + ":" + key);
            filter.writeToByteBuf(filtersBuf);
            api.send(RECIPE_BOOK_FILTERS, player, filtersBuf);
        });
        api.send(RECIPE_BOOK_FILTERS_END, player);
    }

    protected void writeNamespacedKeyList(List<String> values, MCByteBuf byteBuf) {
        byteBuf.writeVarInt(values.size());
        values.forEach(s -> byteBuf.writeUtf(NamespacedKeyUtils.NAMESPACE + ":" + s));
    }
}
