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
import me.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;

public class RecipeCacheSmithing extends RecipeCache<CustomRecipeSmithing> {

    private Ingredient base;
    private Ingredient addition;

    private boolean preserveEnchants;
    private boolean preserveDamage;
    private boolean onlyChangeMaterial;

    RecipeCacheSmithing(CustomCrafting customCrafting) {
        super(customCrafting);
        this.preserveEnchants = true;
        this.preserveDamage = true;
        this.onlyChangeMaterial = false;
    }

    RecipeCacheSmithing(CustomCrafting customCrafting, CustomRecipeSmithing recipe) {
        super(customCrafting, recipe);
        this.base = recipe.getBase().clone();
        this.addition = recipe.getAddition().clone();
        this.preserveEnchants = recipe.isPreserveEnchants();
        this.preserveDamage = recipe.isPreserveDamage();
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        if (slot == 0) {
            setBase(ingredient);
        } else {
            setAddition(ingredient);
        }
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? getBase() : getAddition();
    }

    @Override
    protected CustomRecipeSmithing constructRecipe() {
        return create(new CustomRecipeSmithing(key));
    }

    @Override
    protected CustomRecipeSmithing create(CustomRecipeSmithing recipe) {
        CustomRecipeSmithing recipeSmithing = super.create(recipe);
        recipeSmithing.setBase(base);
        recipeSmithing.setAddition(addition);

        recipeSmithing.setPreserveEnchants(preserveEnchants);
        recipeSmithing.setPreserveDamage(preserveDamage);
        recipeSmithing.setOnlyChangeMaterial(onlyChangeMaterial);
        return recipeSmithing;
    }

    public Ingredient getBase() {
        return base;
    }

    public void setBase(Ingredient base) {
        this.base = base;
    }

    public Ingredient getAddition() {
        return addition;
    }

    public void setAddition(Ingredient addition) {
        this.addition = addition;
    }

    public boolean isPreserveEnchants() {
        return preserveEnchants;
    }

    public void setPreserveEnchants(boolean preserveEnchants) {
        this.preserveEnchants = preserveEnchants;
    }

    public boolean isPreserveDamage() {
        return preserveDamage;
    }

    public void setPreserveDamage(boolean preserveDamage) {
        this.preserveDamage = preserveDamage;
    }

    public void setOnlyChangeMaterial(boolean onlyChangeMaterial) {
        this.onlyChangeMaterial = onlyChangeMaterial;
    }

    public boolean isOnlyChangeMaterial() {
        return onlyChangeMaterial;
    }
}
