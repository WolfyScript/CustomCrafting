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

import me.wolfyscript.customcrafting.recipes.items.RecipeItemStack;

public class TagSettingsCache {

    private int listPage;
    private int chooseListPage;
    private RecipeItemStack recipeItemStack;

    public TagSettingsCache() {
        this.chooseListPage = 0;
        this.listPage = 0;
        this.recipeItemStack = null;
    }

    public int getListPage() {
        return listPage;
    }

    public void setListPage(int listPage) {
        this.listPage = listPage;
    }

    public int getChooseListPage() {
        return chooseListPage;
    }

    public void setChooseListPage(int chooseListPage) {
        this.chooseListPage = chooseListPage;
    }

    public RecipeItemStack getRecipeItemStack() {
        return recipeItemStack;
    }

    public void setRecipeItemStack(RecipeItemStack recipeItemStack) {
        this.recipeItemStack = recipeItemStack;
    }
}
