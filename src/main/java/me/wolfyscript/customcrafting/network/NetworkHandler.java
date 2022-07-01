/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.network;

import io.netty.buffer.Unpooled;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NetworkHandler {

    private static final int MAX_PACKET_SIZE = 29696;

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
        List<CustomRecipe<?>> recipes = customCrafting.getRegistries().getRecipes().getAvailable(player);
        //Send size of recipe list! Client will wait for recipe packets after it receives this packet.
        var mcByteBuf = networkUtil.buffer();
        mcByteBuf.writeVarInt(recipes.size());
        api.send(RECIPE_LIST_SIZE, player, mcByteBuf);
        //Send recipes
        for (CustomRecipe<?> recipe : recipes) {
            var recipeBuf = networkUtil.buffer();
            recipe.writeToBuf(recipeBuf);
            int readableBytes = recipeBuf.readableBytes();
            int fullSlices = readableBytes / MAX_PACKET_SIZE;
            int remainingBytes = readableBytes % MAX_PACKET_SIZE;
            int slices = fullSlices + (remainingBytes > 0 ? 1 : 0);
            recipeBuf.readerIndex(0);
            for (int slice = 0; slice < slices; slice++) {
                readableBytes = recipeBuf.readableBytes();
                //creating a new buffer for the slice.
                if (slice == 0) { //If it is the first slice send the amount of slices first.
                    api.send(RECIPE_LIST, player, networkUtil.buffer().writeVarInt(slices).writeVarInt(fullSlices * MAX_PACKET_SIZE + remainingBytes));
                }
                var length = Math.min(MAX_PACKET_SIZE, readableBytes);
                var buf = Unpooled.buffer(length, length);
                recipeBuf.readBytes(buf, length);
                api.send(RECIPE_LIST, player, networkUtil.buffer(buf));
            }
        }
        //Send end packet
        api.send(RECIPE_LIST_END, player);
    }

    public void sendRecipeBookSettings(Player player) {
        var categories = customCrafting.getConfigHandler().getRecipeBookConfig();
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
        var recipe = customCrafting.getRegistries().getRecipes().get(key);
        if (recipe != null) {
            var inventory = player.getOpenInventory().getTopInventory();
            boolean validInv = switch (recipe.getRecipeType().getType()) {
                case CRAFTING_SHAPED, CRAFTING_SHAPELESS -> inventory instanceof CraftingInventory;
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
