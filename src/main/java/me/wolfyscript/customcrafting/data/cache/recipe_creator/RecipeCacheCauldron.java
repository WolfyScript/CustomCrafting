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

import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

public class RecipeCacheCauldron extends RecipeCache<CustomRecipeCauldron> {

    private int cookingTime;
    private int waterLevel;
    private float xp;
    private CustomItem handItem;
    private Ingredient ingredients;
    private boolean dropItems;
    private boolean needsFire;
    private boolean needsWater;

    RecipeCacheCauldron() {
        super();
    }

    RecipeCacheCauldron(CustomRecipeCauldron recipe) {
        super(recipe);
        this.cookingTime = recipe.getCookingTime();
        this.waterLevel = recipe.getWaterLevel();
        this.xp = recipe.getXp();
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        setIngredients(ingredient);
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return getIngredients();
    }

    @Override
    protected CustomRecipeCauldron constructRecipe() {
        return create(new CustomRecipeCauldron(key));
    }

    @Override
    protected CustomRecipeCauldron create(CustomRecipeCauldron recipe) {
        CustomRecipeCauldron cauldron = super.create(recipe);
        cauldron.setIngredient(ingredients);
        cauldron.setCookingTime(cookingTime);
        cauldron.setWaterLevel(waterLevel);
        cauldron.setXp(xp);
        cauldron.setHandItem(handItem);
        cauldron.setDropItems(dropItems);
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

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public CustomItem getHandItem() {
        return handItem;
    }

    public void setHandItem(CustomItem handItem) {
        this.handItem = handItem;
    }

    public Ingredient getIngredients() {
        return ingredients;
    }

    public void setIngredients(Ingredient ingredients) {
        this.ingredients = ingredients;
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
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
