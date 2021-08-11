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
    private RecipeCacheAnvil anvilCache = new RecipeCacheAnvil(this);
    private RecipeCacheBrewing brewingCache = new RecipeCacheBrewing(this);
    private RecipeCacheCauldron cauldronCache = new RecipeCacheCauldron(this);
    private RecipeCacheCrafting craftingCache = new RecipeCacheCrafting(this);
    private RecipeCacheCraftingElite eliteCraftingCache = new RecipeCacheCraftingElite(this);
    private RecipeCacheGrinding grindingCache = new RecipeCacheGrinding(this);
    private RecipeCacheSmithing smithingCache = new RecipeCacheSmithing(this);
    private RecipeCacheStonecutting stonecuttingCache = new RecipeCacheStonecutting(this);
    private RecipeCacheCooking cookingCache = new RecipeCacheCooking(this);

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
            case FURNACE, BLAST_FURNACE, SMOKER, CAMPFIRE -> cookingCache;
            case WORKBENCH, WORKBENCH_SHAPED, WORKBENCH_SHAPELESS -> craftingCache;
            case CAULDRON -> cauldronCache;
            case SMITHING -> smithingCache;
            case GRINDSTONE -> grindingCache;
            case STONECUTTER -> stonecuttingCache;
            case BREWING_STAND -> brewingCache;
            case ELITE_WORKBENCH, ELITE_WORKBENCH_SHAPED, ELITE_WORKBENCH_SHAPELESS -> eliteCraftingCache;
        };
    }

    public void loadRecipeIntoCache(ICustomRecipe<?> recipe) throws IllegalArgumentException {
        switch (recipe.getRecipeType().getType()) {
            case ANVIL -> anvilCache = new RecipeCacheAnvil(this, (CustomRecipeAnvil) recipe);
            case FURNACE, BLAST_FURNACE, SMOKER, CAMPFIRE -> cookingCache = new RecipeCacheCooking(this, (CustomRecipeCooking<?, ?>) recipe);
            case WORKBENCH, WORKBENCH_SHAPED, WORKBENCH_SHAPELESS -> craftingCache = new RecipeCacheCrafting(this, (CraftingRecipe<?, AdvancedRecipeSettings>) recipe);
            case CAULDRON -> cauldronCache = new RecipeCacheCauldron(this, (CustomRecipeCauldron) recipe);
            case SMITHING -> smithingCache = new RecipeCacheSmithing(this, (CustomRecipeSmithing) recipe);
            case GRINDSTONE -> grindingCache = new RecipeCacheGrinding(this, (CustomRecipeGrindstone) recipe);
            case STONECUTTER -> stonecuttingCache = new RecipeCacheStonecutting(this, (CustomRecipeStonecutter) recipe);
            case BREWING_STAND -> brewingCache = new RecipeCacheBrewing(this, (CustomRecipeBrewing) recipe);
            case ELITE_WORKBENCH, ELITE_WORKBENCH_SHAPED, ELITE_WORKBENCH_SHAPELESS -> eliteCraftingCache = new RecipeCacheCraftingElite(this, (CraftingRecipe<?, EliteRecipeSettings>) recipe);
            default -> throw new IllegalArgumentException("Unsupported recipe type \"" + recipe.getRecipeType().name() + "\"!");
        }
    }

    public void reset() {


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

    public RecipeCacheCooking getCookingCache() {
        return cookingCache;
    }

    public void setCookingCache(RecipeCacheCooking cookingCache) {
        this.cookingCache = cookingCache;
    }
}
