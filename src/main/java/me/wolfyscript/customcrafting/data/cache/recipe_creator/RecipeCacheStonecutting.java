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
import me.wolfyscript.customcrafting.recipes.CustomRecipeStonecutter;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;

public class RecipeCacheStonecutting extends RecipeCache<CustomRecipeStonecutter> {

    private Ingredient source;

    RecipeCacheStonecutting(CustomCrafting customCrafting) {
        super(customCrafting);
        this.source = new Ingredient();
    }

    RecipeCacheStonecutting(CustomCrafting customCrafting, CustomRecipeStonecutter recipe) {
        super(customCrafting, recipe);
        this.source = recipe.getSource().clone();
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        setSource(ingredient);
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return getSource();
    }

    @Override
    protected CustomRecipeStonecutter constructRecipe() {
        return create(new CustomRecipeStonecutter(key));
    }

    @Override
    protected CustomRecipeStonecutter create(CustomRecipeStonecutter recipe) {
        CustomRecipeStonecutter recipeStonecutter = super.create(recipe);
        recipeStonecutter.setSource(source);
        return recipeStonecutter;
    }

    public Ingredient getSource() {
        return source;
    }

    public void setSource(Ingredient source) {
        this.source = source;
    }
}
