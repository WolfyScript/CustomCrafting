package me.wolfyscript.customcrafting.network;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.StackedContents;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.network.messages.MessageAPI;
import me.wolfyscript.utilities.api.nms.NetworkUtil;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Furnace;
import org.bukkit.block.Smoker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;

import java.util.*;

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

    private static final NamespacedKey PLACE_RECIPE = new NamespacedKey(KEY, "place_recipe");
    private static final NamespacedKey GHOST_RECIPE = new NamespacedKey(KEY, "ghost_recipe");

    private static final Set<UUID> networkPlayers = new HashSet<>();

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

    public boolean isNetworkPlayer(Player player) {
        return networkPlayers.contains(player.getUniqueId());
    }

    public void connectPlayer(Player player) {
        networkPlayers.add(player.getUniqueId());
    }

    public void disconnectPlayer(Player player) {
        networkPlayers.remove(player.getUniqueId());
    }

    public void registerPackets() {
        api.register(DATA_REQUEST, (player, wolfyUtilities1, mcByteBuf) -> {
            //Decode request and verify!
            if (wolfyUtilities.getPermissions().hasPermission(player, "customcrafting.network.receive_data")) {
                connectPlayer(player);
                Bukkit.getScheduler().runTaskLater(wolfyUtilities.getPlugin(), () -> {
                    sendRecipes(player);
                    sendRecipeBookSettings(player);
                }, 3);
            }
        });
        api.register(PLACE_RECIPE, (player, wolfyUtilities1, mcByteBuf) -> {
            if (!player.getGameMode().equals(GameMode.SPECTATOR) && isNetworkPlayer(player) && wolfyUtilities.getPermissions().hasPermission(player, "customcrafting.network.place_recipe")) {
                Bukkit.getScheduler().runTaskLater(wolfyUtilities.getPlugin(), () -> handlePlaceRecipe(player, mcByteBuf), 2);
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
        api.register(GHOST_RECIPE);
    }

    public void sendRecipes(Player player) {
        List<ICustomRecipe<?>> recipes = CCRegistry.RECIPES.getAvailable(player);
        //Send size of recipe list! Client will wait for recipe packets after it receives this packet.
        var mcByteBuf = networkUtil.buffer();
        mcByteBuf.writeVarInt(recipes.size());
        api.send(RECIPE_LIST_SIZE, player, mcByteBuf);
        //Send recipes
        for (ICustomRecipe<?> recipe : recipes) {
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

    public void handlePlaceRecipe(Player player, MCByteBuf byteBuf) {
        var key = byteBuf.readNamespacedKey();
        var recipe = CCRegistry.RECIPES.get(key);
        if (recipe != null) {
            var inventory = player.getOpenInventory().getTopInventory();
            boolean validInv = switch (recipe.getRecipeType().getType()) {
                case WORKBENCH -> inventory instanceof CraftingInventory;
                case FURNACE -> inventory.getHolder() instanceof Furnace;
                case BLAST_FURNACE -> inventory.getHolder() instanceof BlastFurnace;
                case SMOKER -> inventory.getHolder() instanceof Smoker;
                default -> false;
            };
            if (validInv) {
                player.sendMessage("Complete recipe: " + key);
                var stackedContents = new StackedContents(inventory);
                player.getOpenInventory().getBottomInventory().forEach(stackedContents::accountItemStack);
            }
        }
    }
}
