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
import java.util.Arrays;
import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;

public class RecipeCacheCauldron extends RecipeCache<CustomRecipeCauldron> {

    private int cookingTime;
    private int waterLevel;
    private int xp;
    private List<Ingredient> ingredients;
    private Result[] additionalResults;

    private boolean canCookInLava;
    private boolean canCookInWater;
    private int fluidLevel;

    private boolean campfire;
    private boolean soulCampfire;
    private boolean signalFire;

    RecipeCacheCauldron(CustomCrafting customCrafting) {
        super(customCrafting);
        this.xp = 0;
        this.cookingTime = 60;
        this.waterLevel = 1;
        this.ingredients = new ArrayList<>();
        this.additionalResults = new Result[]{ new Result(), new Result(), new Result() };
    }

    RecipeCacheCauldron(CustomCrafting customCrafting, CustomRecipeCauldron recipe) {
        super(customCrafting, recipe);
        this.cookingTime = recipe.getCookingTime();
        this.campfire = recipe.isCampfire();
        this.soulCampfire = recipe.isSoulCampfire();
        this.canCookInWater = recipe.isCanCookInWater();
        this.canCookInLava = recipe.isCanCookInLava();
        this.signalFire = recipe.isSignalFire();
        this.fluidLevel = recipe.getFluidLevel();
        this.xp = recipe.getXp();
        this.additionalResults = Arrays.stream(recipe.getAdditionalResults()).map(result1 -> result1 == null ? null : result1.clone()).toArray(value -> new Result[3]);
        this.ingredients = new ArrayList<>(recipe.getIngredients().stream().map(Ingredient::clone).toList());
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
        cauldron.setAdditionalResults(additionalResults);
        cauldron.addIngredients(ingredients);
        cauldron.setCookingTime(cookingTime);
        cauldron.setCampfire(campfire);
        cauldron.setSoulCampfire(soulCampfire);
        cauldron.setSignalFire(signalFire);
        cauldron.setCanCookInLava(canCookInLava);
        cauldron.setCanCookInWater(canCookInWater);
        cauldron.setFluidLevel(fluidLevel);
        cauldron.setXp(xp);
        return cauldron;
    }

    public Result[] getAdditionalResults() {
        return additionalResults;
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

    public boolean isCanCookInLava() {
        return canCookInLava;
    }

    public void setCanCookInLava(boolean canCookInLava) {
        this.canCookInLava = canCookInLava;
    }

    public boolean isCanCookInWater() {
        return canCookInWater;
    }

    public void setCanCookInWater(boolean canCookInWater) {
        this.canCookInWater = canCookInWater;
    }

    public int getFluidLevel() {
        return fluidLevel;
    }

    public void setFluidLevel(int fluidLevel) {
        this.fluidLevel = fluidLevel;
    }

    public boolean isCampfire() {
        return campfire;
    }

    public void setCampfire(boolean campfire) {
        this.campfire = campfire;
    }

    public boolean isSoulCampfire() {
        return soulCampfire;
    }

    public void setSoulCampfire(boolean soulCampfire) {
        this.soulCampfire = soulCampfire;
    }

    public boolean isSignalFire() {
        return signalFire;
    }

    public void setSignalFire(boolean signalFire) {
        this.signalFire = signalFire;
    }
}
