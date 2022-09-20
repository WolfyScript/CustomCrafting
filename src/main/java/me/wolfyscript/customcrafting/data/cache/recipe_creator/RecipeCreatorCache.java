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
import me.wolfyscript.customcrafting.data.cache.ConditionsCache;
import me.wolfyscript.customcrafting.data.cache.IngredientCache;
import me.wolfyscript.customcrafting.data.cache.TagSettingsCache;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.recipes.CustomRecipeBlasting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeBrewing;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCampfire;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.customcrafting.recipes.CustomRecipeFurnace;
import me.wolfyscript.customcrafting.recipes.CustomRecipeGrindstone;
import me.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import me.wolfyscript.customcrafting.recipes.CustomRecipeSmoking;
import me.wolfyscript.customcrafting.recipes.CustomRecipeStonecutter;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;


public class RecipeCreatorCache {

    private final CustomCrafting customCrafting;

    private final IngredientCache ingredientCache = new IngredientCache();
    private final TagSettingsCache tagSettingsCache = new TagSettingsCache();
    private final ConditionsCache conditionsCache = new ConditionsCache();
    private RecipeType<?> recipeType;

    private RecipeCacheAnvil anvilCache;
    private RecipeCacheBrewing brewingCache;
    private RecipeCacheCauldron cauldronCache;
    private RecipeCacheCrafting craftingCache;
    private RecipeCacheCraftingElite eliteCraftingCache;
    private RecipeCacheGrinding grindingCache;
    private RecipeCacheSmithing smithingCache;
    private RecipeCacheStonecutting stonecuttingCache;
    private RecipeCacheFurnace furnaceCache;
    private RecipeCacheBlasting blastingCache;
    private RecipeCacheSmoking smokerCache;
    private RecipeCacheCampfire campfireCache;

    public RecipeCreatorCache(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.anvilCache = new RecipeCacheAnvil(this.customCrafting);
        this.brewingCache = new RecipeCacheBrewing(this.customCrafting);
        this.cauldronCache = new RecipeCacheCauldron(this.customCrafting);
        this.craftingCache = new RecipeCacheCrafting(this.customCrafting);
        this.eliteCraftingCache = new RecipeCacheCraftingElite(this.customCrafting);
        this.grindingCache = new RecipeCacheGrinding(this.customCrafting);
        this.smithingCache = new RecipeCacheSmithing(this.customCrafting);
        this.stonecuttingCache = new RecipeCacheStonecutting(this.customCrafting);
        this.furnaceCache = new RecipeCacheFurnace(this.customCrafting);
        this.blastingCache = new RecipeCacheBlasting(this.customCrafting);
        this.smokerCache = new RecipeCacheSmoking(this.customCrafting);
        this.campfireCache = new RecipeCacheCampfire(this.customCrafting);
    }

    public RecipeType<?> getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(RecipeType<?> recipeType) {
        this.recipeType = recipeType;
    }

    public RecipeCache<?> getCacheByType(RecipeType<?> type) {
        return switch (type.getType()) {
            case ANVIL -> anvilCache;
            case FURNACE -> furnaceCache;
            case BLAST_FURNACE -> blastingCache;
            case SMOKER -> smokerCache;
            case CAMPFIRE -> campfireCache;
            case CRAFTING_SHAPED, CRAFTING_SHAPELESS -> craftingCache;
            case CAULDRON -> cauldronCache;
            case SMITHING -> smithingCache;
            case GRINDSTONE -> grindingCache;
            case STONECUTTER -> stonecuttingCache;
            case BREWING_STAND -> brewingCache;
            case ELITE_CRAFTING_SHAPED, ELITE_CRAFTING_SHAPELESS -> eliteCraftingCache;
            default -> null;
        };
    }

    public RecipeCacheCooking<?> getCookingCache() {
        return switch (getRecipeType().getType()) {
            case FURNACE -> furnaceCache;
            case BLAST_FURNACE -> blastingCache;
            case SMOKER -> smokerCache;
            case CAMPFIRE -> campfireCache;
            default ->
                    throw new IllegalArgumentException("Recipe type \"" + getRecipeType().name() + "\" is not a cooking recipe type!");
        };
    }

    public void loadRecipeIntoCache(CustomRecipe<?> recipe) throws IllegalArgumentException {
        switch (recipe.getRecipeType().getType()) {
            case ANVIL -> setAnvilCache(new RecipeCacheAnvil(this.customCrafting, (CustomRecipeAnvil) recipe));
            case FURNACE -> setFurnaceCache(new RecipeCacheFurnace(this.customCrafting, (CustomRecipeFurnace) recipe));
            case BLAST_FURNACE -> setBlastingCache(new RecipeCacheBlasting(this.customCrafting, (CustomRecipeBlasting) recipe));
            case CAMPFIRE -> setCampfireCache(new RecipeCacheCampfire(this.customCrafting, (CustomRecipeCampfire) recipe));
            case SMOKER -> setSmokerCache(new RecipeCacheSmoking(this.customCrafting, (CustomRecipeSmoking) recipe));
            case CRAFTING_SHAPED, CRAFTING_SHAPELESS -> setCraftingCache(new RecipeCacheCrafting(this.customCrafting, (CraftingRecipe<?, AdvancedRecipeSettings>) recipe));
            case CAULDRON -> setCauldronCache(new RecipeCacheCauldron(this.customCrafting, (CustomRecipeCauldron) recipe));
            case SMITHING -> setSmithingCache(new RecipeCacheSmithing(this.customCrafting, (CustomRecipeSmithing) recipe));
            case GRINDSTONE -> setGrindingCache(new RecipeCacheGrinding(this.customCrafting, (CustomRecipeGrindstone) recipe));
            case STONECUTTER -> setStonecuttingCache(new RecipeCacheStonecutting(this.customCrafting, (CustomRecipeStonecutter) recipe));
            case BREWING_STAND -> setBrewingCache(new RecipeCacheBrewing(this.customCrafting, (CustomRecipeBrewing) recipe));
            case ELITE_CRAFTING_SHAPED, ELITE_CRAFTING_SHAPELESS -> setEliteCraftingCache(new RecipeCacheCraftingElite(this.customCrafting, (CraftingRecipe<?, EliteRecipeSettings>) recipe));
            default -> throw new IllegalArgumentException("Unsupported recipe type \"" + recipe.getRecipeType().name() + "\"!");
        }
    }

    public void reset() {
        switch (getRecipeType().getType()) {
            case ANVIL -> setAnvilCache(new RecipeCacheAnvil(this.customCrafting));
            case FURNACE -> setFurnaceCache(new RecipeCacheFurnace(this.customCrafting));
            case BLAST_FURNACE -> setBlastingCache(new RecipeCacheBlasting(this.customCrafting));
            case CAMPFIRE -> setCampfireCache(new RecipeCacheCampfire(this.customCrafting));
            case SMOKER -> setSmokerCache(new RecipeCacheSmoking(this.customCrafting));
            case CRAFTING_SHAPED, CRAFTING_SHAPELESS -> setCraftingCache(new RecipeCacheCrafting(this.customCrafting));
            case CAULDRON -> setCauldronCache(new RecipeCacheCauldron(this.customCrafting));
            case SMITHING -> setSmithingCache(new RecipeCacheSmithing(this.customCrafting));
            case GRINDSTONE -> setGrindingCache(new RecipeCacheGrinding(this.customCrafting));
            case STONECUTTER -> setStonecuttingCache(new RecipeCacheStonecutting(this.customCrafting));
            case BREWING_STAND -> setBrewingCache(new RecipeCacheBrewing(this.customCrafting));
            case ELITE_CRAFTING_SHAPED, ELITE_CRAFTING_SHAPELESS -> setEliteCraftingCache(new RecipeCacheCraftingElite(this.customCrafting));
            default -> throw new IllegalArgumentException("Unsupported recipe type \"" + getRecipeType().name() + "\"!");
        }
    }

    public IngredientCache getIngredientCache() {
        return ingredientCache;
    }

    public ConditionsCache getConditionsCache() {
        return conditionsCache;
    }

    public TagSettingsCache getTagSettingsCache() {
        return tagSettingsCache;
    }

    public RecipeCache<?> getRecipeCache() {
        return getCacheByType(getRecipeType());
    }

    public RecipeCacheAnvil getAnvilCache() {
        return anvilCache;
    }

    public void setAnvilCache(RecipeCacheAnvil anvilCache) {
        this.anvilCache = anvilCache;
    }

    public RecipeCacheBrewing getBrewingCache() {
        return brewingCache;
    }

    public void setBrewingCache(RecipeCacheBrewing brewingCache) {
        this.brewingCache = brewingCache;
    }

    public RecipeCacheCauldron getCauldronCache() {
        return cauldronCache;
    }

    public void setCauldronCache(RecipeCacheCauldron cauldronCache) {
        this.cauldronCache = cauldronCache;
    }

    public RecipeCacheCrafting getCraftingCache() {
        return craftingCache;
    }

    public void setCraftingCache(RecipeCacheCrafting craftingCache) {
        this.craftingCache = craftingCache;
    }

    public RecipeCacheCraftingElite getEliteCraftingCache() {
        return eliteCraftingCache;
    }

    public void setEliteCraftingCache(RecipeCacheCraftingElite eliteCraftingCache) {
        this.eliteCraftingCache = eliteCraftingCache;
    }

    public RecipeCacheGrinding getGrindingCache() {
        return grindingCache;
    }

    public void setGrindingCache(RecipeCacheGrinding grindingCache) {
        this.grindingCache = grindingCache;
    }

    public RecipeCacheSmithing getSmithingCache() {
        return smithingCache;
    }

    public void setSmithingCache(RecipeCacheSmithing smithingCache) {
        this.smithingCache = smithingCache;
    }

    public RecipeCacheStonecutting getStonecuttingCache() {
        return stonecuttingCache;
    }

    public void setStonecuttingCache(RecipeCacheStonecutting stonecuttingCache) {
        this.stonecuttingCache = stonecuttingCache;
    }

    public RecipeCacheBlasting getBlastingCache() {
        return blastingCache;
    }

    public void setBlastingCache(RecipeCacheBlasting blastingCache) {
        this.blastingCache = blastingCache;
    }

    public RecipeCacheFurnace getFurnaceCache() {
        return furnaceCache;
    }

    public void setFurnaceCache(RecipeCacheFurnace furnaceCache) {
        this.furnaceCache = furnaceCache;
    }

    public RecipeCacheCampfire getCampfireCache() {
        return campfireCache;
    }

    public void setCampfireCache(RecipeCacheCampfire campfireCache) {
        this.campfireCache = campfireCache;
    }

    public RecipeCacheSmoking getSmokerCache() {
        return smokerCache;
    }

    public void setSmokerCache(RecipeCacheSmoking smokerCache) {
        this.smokerCache = smokerCache;
    }
}
