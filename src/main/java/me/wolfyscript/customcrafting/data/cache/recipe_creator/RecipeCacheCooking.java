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

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;

public abstract class RecipeCacheCooking<R extends CustomRecipeCooking<R, ?>> extends RecipeCache<R> {

    private Ingredient source;
    private float exp;
    private int cookingTime;

    protected RecipeCacheCooking(CustomCrafting customCrafting) {
        super(customCrafting);
        this.checkAllNBT = true;
        this.source = new Ingredient();
        this.exp = 0;
        this.cookingTime = 80;
    }

    protected RecipeCacheCooking(CustomCrafting customCrafting, R recipe) {
        super(customCrafting, recipe);
        this.checkAllNBT = true;
        this.source = recipe.getSource();
        this.exp = recipe.getExp();
        this.cookingTime = recipe.getCookingTime();
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        setSource(ingredient);
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return getSource();
    }

    public Ingredient getSource() {
        return source;
    }

    public void setSource(Ingredient source) {
        this.source = source;
    }

    @Override
    protected R create(R recipe) {
        R cookingRecipe = super.create(recipe);
        cookingRecipe.setCookingTime(cookingTime);
        cookingRecipe.setSource(source);
        cookingRecipe.setExp(exp);
        return cookingRecipe;
    }

    public float getExp() {
        return exp;
    }

    public void setExp(float exp) {
        this.exp = exp;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        Preconditions.checkArgument(cookingTime <= Short.MAX_VALUE, "The cooking time cannot be higher than 32767.");
        this.cookingTime = cookingTime;
    }
}
