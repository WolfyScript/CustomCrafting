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

package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeGrindstone;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;

public class RecipeCacheGrinding extends RecipeCache<CustomRecipeGrindstone> {

    private Ingredient inputTop;
    private Ingredient inputBottom;
    private int xp;

    RecipeCacheGrinding(CustomCrafting customCrafting) {
        super(customCrafting);
        this.inputTop = new Ingredient();
        this.inputBottom = new Ingredient();
    }

    RecipeCacheGrinding(CustomCrafting customCrafting, CustomRecipeGrindstone recipe) {
        super(customCrafting, recipe);
        this.inputTop = recipe.getInputTop().clone();
        this.inputBottom = recipe.getInputBottom().clone();
        this.xp = recipe.getXp();
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        if (slot == 0) {
            setInputTop(ingredient);
        } else {
            setInputBottom(ingredient);
        }
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? getInputTop() : getInputBottom();
    }

    @Override
    protected CustomRecipeGrindstone constructRecipe() {
        return create(new CustomRecipeGrindstone(key, inputTop, inputBottom));
    }

    @Override
    protected CustomRecipeGrindstone create(CustomRecipeGrindstone recipe) {
        CustomRecipeGrindstone recipeGrinding = super.create(recipe);
        recipeGrinding.setXp(xp);
        return recipeGrinding;
    }

    public Ingredient getInputTop() {
        return inputTop;
    }

    public void setInputTop(Ingredient inputTop) {
        this.inputTop = inputTop;
    }

    public Ingredient getInputBottom() {
        return inputBottom;
    }

    public void setInputBottom(Ingredient inputBottom) {
        this.inputBottom = inputBottom;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
