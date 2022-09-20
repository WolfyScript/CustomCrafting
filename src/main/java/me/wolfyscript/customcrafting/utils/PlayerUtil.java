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

package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.entity.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerUtil {

    private PlayerUtil() {
    }

    public static final NamespacedKey CC_DATA = new NamespacedKey("customcrafting", "data");

    public static CCPlayerData getStore(Player player) {
        return getStore(player.getUniqueId());
    }

    public static CCPlayerData getStore(UUID uuid) {
        return PlayerUtils.getStore(uuid).getData(CC_DATA, CCPlayerData.class);
    }

    public static void openRecipeBook(Player player) {
        CustomCrafting customCrafting = CustomCrafting.inst();
        InventoryAPI<CCCache> invAPI = customCrafting.getApi().getInventoryAPI(CCCache.class);
        var categories = customCrafting.getConfigHandler().getRecipeBookConfig();
        var bookCache = invAPI.getGuiHandler(player).getCustomCache().getRecipeBookCache();

        //Reset the pages etc. so it opens up the recipe overview again
        if (!customCrafting.getConfigHandler().getConfig().isRecipeBookKeepLastOpen()) {
            bookCache.setResearchItems(new ArrayList<>());
            bookCache.setSubFolderPage(0);
            bookCache.setPage(0);
        }

        // Open directly to the category if we only have one
        bookCache.setPrepareRecipe(true);
        if (categories.getSortedCategories().size() == 1) {
            bookCache.setCategory(categories.getCategory(0));
            invAPI.openGui(player, ClusterRecipeBook.RECIPE_BOOK);
        } else if (!categories.getSortedCategories().isEmpty()) {
            invAPI.openCluster(player, ClusterRecipeBook.KEY);
        }
    }

}
