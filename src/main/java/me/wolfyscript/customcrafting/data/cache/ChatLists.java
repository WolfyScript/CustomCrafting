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

package me.wolfyscript.customcrafting.data.cache;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;

public class ChatLists {

    private int currentPageRecipes;
    private int currentPageItems;

    private String lastUsedRecipe;
    private NamespacedKey lastUsedItem;

    public ChatLists() {
        this.currentPageRecipes = 1;
        this.currentPageItems = 1;
        this.lastUsedRecipe = "";
        this.lastUsedItem = null;
    }

    public int getCurrentPageRecipes() {
        return currentPageRecipes;
    }

    public void setCurrentPageRecipes(int currentPageRecipes) {
        this.currentPageRecipes = currentPageRecipes;
    }

    public int getCurrentPageItems() {
        return currentPageItems;
    }

    public void setCurrentPageItems(int currentPageItems) {
        this.currentPageItems = currentPageItems;
    }

    public String getLastUsedRecipe() {
        return lastUsedRecipe;
    }

    public void setLastUsedRecipe(String lastUsedRecipe) {
        this.lastUsedRecipe = lastUsedRecipe;
    }

    public NamespacedKey getLastUsedItem() {
        return lastUsedItem;
    }

    public void setLastUsedItem(NamespacedKey lastUsedItem) {
        this.lastUsedItem = lastUsedItem;
    }
}
