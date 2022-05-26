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
import me.wolfyscript.customcrafting.recipes.CustomRecipeBrewing;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeCacheBrewing extends RecipeCache<CustomRecipeBrewing> {

    Ingredient allowedItems; //The CustomItems that can be used. Needs to be a potion of course.
    private Ingredient ingredients; //The top ingredient of the recipe. Always required.
    private int fuelCost; //The fuel cost of recipe
    private int brewTime; //The brew time in ticks

    //These options are for general changes made to the potions, if advanced features are not required, or you want to edit all effects before editing them further in detail.
    private int durationChange; //added to the Duration. if <0 it will be subtracted
    private int amplifierChange; //added to the Amplifier. if <0 it will be subtracted
    private boolean resetEffects; //If true resets all the effects
    private Color effectColor; //Alternative to colorChange

    //These options are more precise, and you can specify the exact effect you want to edit.
    private List<PotionEffectType> effectRemovals; //These effects will be removed from the potions
    private Map<PotionEffect, Boolean> effectAdditions; //These effects will be added with an option if they should be replaced if they are already present
    private Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades; //These effects will be added to the existing potion effects. Meaning that the values of these PotionEffects will add to the existing effects and boolean values will be replaced.
    //Instead of all these options you can use a set result.

    //Conditions for the Potions inside the 3 slots at the bottom
    private Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects; //The effects that are required with the current Duration and amplitude. Integer values == 0 will be ignored and any value will be allowed.

    RecipeCacheBrewing(CustomCrafting customCrafting) {
        super(customCrafting);
        allowedItems = new Ingredient();
        ingredients = new Ingredient();
        fuelCost = 1;
        brewTime = 80;
        durationChange = 0;
        amplifierChange = 0;
        resetEffects = false;
        effectColor = null;
        effectRemovals = new ArrayList<>();
        effectAdditions = new HashMap<>();
        effectUpgrades = new HashMap<>();
        requiredEffects = new HashMap<>();
    }

    RecipeCacheBrewing(CustomCrafting customCrafting, CustomRecipeBrewing recipe) {
        super(customCrafting, recipe);
        this.allowedItems = recipe.getAllowedItems().clone();
        this.ingredients = recipe.getIngredient().clone();
        this.fuelCost = recipe.getFuelCost();
        this.brewTime = recipe.getBrewTime();

        this.durationChange = recipe.getDurationChange();
        this.amplifierChange = recipe.getAmplifierChange();
        this.resetEffects = recipe.isResetEffects();
        this.effectColor = recipe.getEffectColor();

        this.effectRemovals = recipe.getEffectRemovals();
        this.effectAdditions = recipe.getEffectAdditions();
        this.effectUpgrades = recipe.getEffectUpgrades();

        this.requiredEffects = recipe.getRequiredEffects();
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        if (slot == 0) {
            this.ingredients = ingredient;
        } else {
            this.allowedItems = ingredient;
        }
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? this.ingredients : this.allowedItems;
    }

    @Override
    protected CustomRecipeBrewing constructRecipe() {
        return create(new CustomRecipeBrewing(key));
    }

    @Override
    protected CustomRecipeBrewing create(CustomRecipeBrewing recipe) {
        CustomRecipeBrewing brewing = super.create(recipe);
        brewing.setIngredient(ingredients);
        brewing.setAllowedItems(allowedItems);
        brewing.setFuelCost(fuelCost);
        brewing.setBrewTime(brewTime);
        brewing.setDurationChange(durationChange);
        brewing.setAmplifierChange(amplifierChange);
        brewing.setResetEffects(resetEffects);
        brewing.setEffectColor(effectColor);
        brewing.setEffectRemovals(effectRemovals);
        brewing.setEffectAdditions(effectAdditions);
        brewing.setEffectUpgrades(effectUpgrades);
        brewing.setRequiredEffects(requiredEffects);
        return brewing;
    }

    public Ingredient getAllowedItems() {
        return allowedItems;
    }

    public void setAllowedItems(Ingredient allowedItems) {
        this.allowedItems = allowedItems;
    }

    public Ingredient getIngredients() {
        return ingredients;
    }

    public void setIngredients(Ingredient ingredients) {
        this.ingredients = ingredients;
    }

    public int getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(int fuelCost) {
        this.fuelCost = fuelCost;
    }

    public int getBrewTime() {
        return brewTime;
    }

    public void setBrewTime(int brewTime) {
        this.brewTime = brewTime;
    }

    public int getDurationChange() {
        return durationChange;
    }

    public void setDurationChange(int durationChange) {
        this.durationChange = durationChange;
    }

    public int getAmplifierChange() {
        return amplifierChange;
    }

    public void setAmplifierChange(int amplifierChange) {
        this.amplifierChange = amplifierChange;
    }

    public boolean isResetEffects() {
        return resetEffects;
    }

    public void setResetEffects(boolean resetEffects) {
        this.resetEffects = resetEffects;
    }

    public Color getEffectColor() {
        return effectColor;
    }

    public void setEffectColor(Color effectColor) {
        this.effectColor = effectColor;
    }

    public List<PotionEffectType> getEffectRemovals() {
        return effectRemovals;
    }

    public void setEffectRemovals(List<PotionEffectType> effectRemovals) {
        this.effectRemovals = effectRemovals;
    }

    public Map<PotionEffect, Boolean> getEffectAdditions() {
        return effectAdditions;
    }

    public void setEffectAdditions(Map<PotionEffect, Boolean> effectAdditions) {
        this.effectAdditions = effectAdditions;
    }

    public Map<PotionEffectType, Pair<Integer, Integer>> getEffectUpgrades() {
        return effectUpgrades;
    }

    public void setEffectUpgrades(Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades) {
        this.effectUpgrades = effectUpgrades;
    }

    public Map<PotionEffectType, Pair<Integer, Integer>> getRequiredEffects() {
        return requiredEffects;
    }

    public void setRequiredEffects(Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects) {
        this.requiredEffects = requiredEffects;
    }
}
