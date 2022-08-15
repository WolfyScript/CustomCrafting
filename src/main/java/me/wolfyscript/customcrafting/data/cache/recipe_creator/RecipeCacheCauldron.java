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

import java.util.ArrayList;
import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

public class RecipeCacheCauldron extends RecipeCache<CustomRecipeCauldron> {

    private int cookingTime;
    private int waterLevel;
    private int xp;
    private List<Ingredient> ingredients;
    private boolean needsFire;
    private boolean needsWater;

    RecipeCacheCauldron(CustomCrafting customCrafting) {
        super(customCrafting);
        this.xp = 0;
        this.cookingTime = 60;
        this.waterLevel = 1;
        this.needsWater = true;
        this.needsFire = true;
        this.ingredients = new ArrayList<>();
    }

    RecipeCacheCauldron(CustomCrafting customCrafting, CustomRecipeCauldron recipe) {
        super(customCrafting, recipe);
        this.cookingTime = recipe.getCookingTime();
        this.waterLevel = recipe.getWaterLevel();
        this.xp = recipe.getXp();
        this.ingredients = new ArrayList<>(recipe.getIngredients().stream().map(Ingredient::clone).toList());
        this.needsFire = recipe.needsFire();
        this.needsWater = recipe.needsWater();
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        if (slot < ingredients.size()) {
            ingredients.set(slot, ingredient);
        } else if (ingredients.size() < 6){
            ingredients.add(ingredient);
        }
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot < ingredients.size() ? ingredients.get(slot) : null;
    }

    @Override
    protected CustomRecipeCauldron constructRecipe() {
        return create(new CustomRecipeCauldron(key));
    }

    @Override
    protected CustomRecipeCauldron create(CustomRecipeCauldron recipe) {
        CustomRecipeCauldron cauldron = super.create(recipe);
        cauldron.addIngredients(ingredients);
        cauldron.setCookingTime(cookingTime);
        cauldron.setWaterLevel(waterLevel);
        cauldron.setXp(xp);
        cauldron.setNeedsFire(needsFire);
        cauldron.setNeedsWater(needsWater);
        return cauldron;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public boolean isNeedsFire() {
        return needsFire;
    }

    public void setNeedsFire(boolean needsFire) {
        this.needsFire = needsFire;
    }

    public boolean isNeedsWater() {
        return needsWater;
    }

    public void setNeedsWater(boolean needsWater) {
        this.needsWater = needsWater;
    }
}
