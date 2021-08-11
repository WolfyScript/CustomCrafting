package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.data.cache.ConditionsCache;
import me.wolfyscript.customcrafting.data.cache.IngredientCache;
import me.wolfyscript.customcrafting.data.cache.TagSettingsCache;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;


public class RecipeCreatorCache {

    private final IngredientCache ingredientCache = new IngredientCache();
    private final TagSettingsCache tagSettingsCache = new TagSettingsCache();
    private final ConditionsCache conditionsCache = new ConditionsCache();
    private RecipeType<?> recipeType;

    private RecipeCacheAnvil anvilCache = new RecipeCacheAnvil();
    private RecipeCacheBrewing brewingCache = new RecipeCacheBrewing();
    private RecipeCacheCauldron cauldronCache = new RecipeCacheCauldron();
    private RecipeCacheCrafting craftingCache = new RecipeCacheCrafting();
    private RecipeCacheCraftingElite eliteCraftingCache = new RecipeCacheCraftingElite();
    private RecipeCacheGrinding grindingCache = new RecipeCacheGrinding();
    private RecipeCacheSmithing smithingCache = new RecipeCacheSmithing();
    private RecipeCacheStonecutting stonecuttingCache = new RecipeCacheStonecutting();
    private RecipeCacheFurnace furnaceCache = new RecipeCacheFurnace();
    private RecipeCacheBlasting blastingCache = new RecipeCacheBlasting();
    private RecipeCacheSmoking smokerCache = new RecipeCacheSmoking();
    private RecipeCacheCampfire campfireCache = new RecipeCacheCampfire();

    public RecipeCreatorCache() {

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
            case WORKBENCH, WORKBENCH_SHAPED, WORKBENCH_SHAPELESS -> craftingCache;
            case CAULDRON -> cauldronCache;
            case SMITHING -> smithingCache;
            case GRINDSTONE -> grindingCache;
            case STONECUTTER -> stonecuttingCache;
            case BREWING_STAND -> brewingCache;
            case ELITE_WORKBENCH, ELITE_WORKBENCH_SHAPED, ELITE_WORKBENCH_SHAPELESS -> eliteCraftingCache;
        };
    }

    public RecipeCacheCooking<?> getCookingCache() {
        return switch (getRecipeType().getType()) {
            case FURNACE -> furnaceCache;
            case BLAST_FURNACE -> blastingCache;
            case SMOKER -> smokerCache;
            case CAMPFIRE -> campfireCache;
            default -> throw new IllegalArgumentException("Recipe type \"" + getRecipeType().name() + "\" is not a cooking recipe type!");
        };
    }

    public void loadRecipeIntoCache(ICustomRecipe<?> recipe) throws IllegalArgumentException {
        switch (recipe.getRecipeType().getType()) {
            case ANVIL -> setAnvilCache(new RecipeCacheAnvil((CustomRecipeAnvil) recipe));
            case FURNACE -> setFurnaceCache(new RecipeCacheFurnace((CustomRecipeFurnace) recipe));
            case BLAST_FURNACE -> setBlastingCache(new RecipeCacheBlasting((CustomRecipeBlasting) recipe));
            case CAMPFIRE -> setCampfireCache(new RecipeCacheCampfire((CustomRecipeCampfire) recipe));
            case SMOKER -> setSmokerCache(new RecipeCacheSmoking((CustomRecipeSmoking) recipe));
            case WORKBENCH, WORKBENCH_SHAPED, WORKBENCH_SHAPELESS -> setCraftingCache(new RecipeCacheCrafting((CraftingRecipe<?, AdvancedRecipeSettings>) recipe));
            case CAULDRON -> setCauldronCache(new RecipeCacheCauldron((CustomRecipeCauldron) recipe));
            case SMITHING -> setSmithingCache(new RecipeCacheSmithing((CustomRecipeSmithing) recipe));
            case GRINDSTONE -> setGrindingCache(new RecipeCacheGrinding((CustomRecipeGrindstone) recipe));
            case STONECUTTER -> setStonecuttingCache(new RecipeCacheStonecutting((CustomRecipeStonecutter) recipe));
            case BREWING_STAND -> setBrewingCache(new RecipeCacheBrewing((CustomRecipeBrewing) recipe));
            case ELITE_WORKBENCH, ELITE_WORKBENCH_SHAPED, ELITE_WORKBENCH_SHAPELESS -> setEliteCraftingCache(new RecipeCacheCraftingElite((CraftingRecipe<?, EliteRecipeSettings>) recipe));
            default -> throw new IllegalArgumentException("Unsupported recipe type \"" + recipe.getRecipeType().name() + "\"!");
        }
    }

    public void reset() {
        switch (getRecipeType().getType()) {
            case ANVIL -> setAnvilCache(new RecipeCacheAnvil());
            case FURNACE -> setFurnaceCache(new RecipeCacheFurnace());
            case BLAST_FURNACE -> setBlastingCache(new RecipeCacheBlasting());
            case CAMPFIRE -> setCampfireCache(new RecipeCacheCampfire());
            case SMOKER -> setSmokerCache(new RecipeCacheSmoking());
            case WORKBENCH, WORKBENCH_SHAPED, WORKBENCH_SHAPELESS -> setCraftingCache(new RecipeCacheCrafting());
            case CAULDRON -> setCauldronCache(new RecipeCacheCauldron());
            case SMITHING -> setSmithingCache(new RecipeCacheSmithing());
            case GRINDSTONE -> setGrindingCache(new RecipeCacheGrinding());
            case STONECUTTER -> setStonecuttingCache(new RecipeCacheStonecutting());
            case BREWING_STAND -> setBrewingCache(new RecipeCacheBrewing());
            case ELITE_WORKBENCH, ELITE_WORKBENCH_SHAPED, ELITE_WORKBENCH_SHAPELESS -> setEliteCraftingCache(new RecipeCacheCraftingElite());
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
