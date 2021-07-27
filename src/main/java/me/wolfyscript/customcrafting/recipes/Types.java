package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.types.*;
import me.wolfyscript.customcrafting.recipes.types.crafting.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Constants for recipe types. They contain the information which class the recipe is from and the creator id, etc.
 */
public class Types {

    static final Set<RecipeType<? extends ICustomRecipe<?, ?>>> values = new HashSet<>();
    public enum Type {
        WORKBENCH,
        WORKBENCH_SHAPED,
        WORKBENCH_SHAPELESS,
        ELITE_WORKBENCH,
        ELITE_WORKBENCH_SHAPED,
        ELITE_WORKBENCH_SHAPELESS,
        ANVIL,
        FURNACE,
        BLAST_FURNACE,
        SMOKER,
        CAMPFIRE,
        STONECUTTER,
        CAULDRON,
        GRINDSTONE,
        BREWING_STAND,
        SMITHING
    }

    public static final RecipeType<ShapedCraftRecipe> WORKBENCH_SHAPED = new RecipeType<>(Type.WORKBENCH_SHAPED, ShapedCraftRecipe.class);
    public static final RecipeType<ShapelessCraftRecipe> WORKBENCH_SHAPELESS = new RecipeType<>(Type.WORKBENCH_SHAPELESS, ShapelessCraftRecipe.class);
    public static final RecipeType.CraftingRecipeType<AdvancedRecipeSettings, ShapedCraftRecipe, ShapelessCraftRecipe> WORKBENCH = new RecipeType.CraftingRecipeType<>(Type.WORKBENCH, AdvancedRecipeSettings.class, WORKBENCH_SHAPED, WORKBENCH_SHAPELESS);

    public static final RecipeType<ShapedEliteCraftRecipe> ELITE_WORKBENCH_SHAPED = new RecipeType<>(Type.ELITE_WORKBENCH_SHAPED, ShapedEliteCraftRecipe.class);
    public static final RecipeType<ShapelessEliteCraftRecipe> ELITE_WORKBENCH_SHAPELESS = new RecipeType<>(Type.ELITE_WORKBENCH_SHAPELESS, ShapelessEliteCraftRecipe.class);
    public static final RecipeType.CraftingRecipeType<EliteRecipeSettings, ShapedEliteCraftRecipe, ShapelessEliteCraftRecipe> ELITE_WORKBENCH = new RecipeType.CraftingRecipeType<>(Type.ELITE_WORKBENCH, EliteRecipeSettings.class, ELITE_WORKBENCH_SHAPED, ELITE_WORKBENCH_SHAPELESS);

    public static final RecipeType.CookingRecipeType<CustomFurnaceRecipe> FURNACE = new RecipeType.CookingRecipeType<>(Type.FURNACE, CustomFurnaceRecipe.class);
    public static final RecipeType.CookingRecipeType<CustomBlastRecipe> BLAST_FURNACE = new RecipeType.CookingRecipeType<>(Type.BLAST_FURNACE, CustomBlastRecipe.class);
    public static final RecipeType.CookingRecipeType<CustomSmokerRecipe> SMOKER = new RecipeType.CookingRecipeType<>(Type.SMOKER, CustomSmokerRecipe.class);
    public static final RecipeType.CookingRecipeType<CustomCampfireRecipe> CAMPFIRE = new RecipeType.CookingRecipeType<>(Type.CAMPFIRE, CustomCampfireRecipe.class);
    public static final RecipeType<CustomAnvilRecipe> ANVIL = new RecipeType<>(Type.ANVIL, CustomAnvilRecipe.class);
    public static final RecipeType<CustomStonecutterRecipe> STONECUTTER = new RecipeType<>(Type.STONECUTTER, CustomStonecutterRecipe.class);
    public static final RecipeType<CauldronRecipe> CAULDRON = new RecipeType<>(Type.CAULDRON, CauldronRecipe.class);
    public static final RecipeType<GrindstoneRecipe> GRINDSTONE = new RecipeType<>(Type.GRINDSTONE, GrindstoneRecipe.class);
    public static final RecipeType<BrewingRecipe> BREWING_STAND = new RecipeType<>(Type.BREWING_STAND, BrewingRecipe.class);
    public static final RecipeType<CustomSmithingRecipe> SMITHING = new RecipeType<>(Type.SMITHING, CustomSmithingRecipe.class);

    public static Set<RecipeType<? extends ICustomRecipe<?,?>>> values() {
        return Collections.unmodifiableSet(values);
    }

    public static RecipeType<?> valueOf(String id){
        return Types.values.stream().filter(rType -> rType.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public static RecipeType<?> valueOf(Types.Type type){
        return Types.values.stream().filter(rType -> rType.getType().equals(type)).findFirst().orElse(null);
    }
}
