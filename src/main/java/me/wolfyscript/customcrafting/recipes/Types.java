package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;

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

    public static final RecipeType<CustomRecipeShaped> WORKBENCH_SHAPED = new RecipeType<>(Type.WORKBENCH_SHAPED, CustomRecipeShaped.class);
    public static final RecipeType<CustomRecipeShapeless> WORKBENCH_SHAPELESS = new RecipeType<>(Type.WORKBENCH_SHAPELESS, CustomRecipeShapeless.class);
    public static final RecipeType.CraftingRecipeType<AdvancedRecipeSettings, CustomRecipeShaped, CustomRecipeShapeless> WORKBENCH = new RecipeType.CraftingRecipeType<>(Type.WORKBENCH, AdvancedRecipeSettings.class, WORKBENCH_SHAPED, WORKBENCH_SHAPELESS);

    public static final RecipeType<CustomRecipeShapedElite> ELITE_WORKBENCH_SHAPED = new RecipeType<>(Type.ELITE_WORKBENCH_SHAPED, CustomRecipeShapedElite.class);
    public static final RecipeType<CustomRecipeShapelessElite> ELITE_WORKBENCH_SHAPELESS = new RecipeType<>(Type.ELITE_WORKBENCH_SHAPELESS, CustomRecipeShapelessElite.class);
    public static final RecipeType.CraftingRecipeType<EliteRecipeSettings, CustomRecipeShapedElite, CustomRecipeShapelessElite> ELITE_WORKBENCH = new RecipeType.CraftingRecipeType<>(Type.ELITE_WORKBENCH, EliteRecipeSettings.class, ELITE_WORKBENCH_SHAPED, ELITE_WORKBENCH_SHAPELESS);

    public static final RecipeType.CookingRecipeType<CustomRecipeFurnace> FURNACE = new RecipeType.CookingRecipeType<>(Type.FURNACE, CustomRecipeFurnace.class);
    public static final RecipeType.CookingRecipeType<CustomRecipeBlasting> BLAST_FURNACE = new RecipeType.CookingRecipeType<>(Type.BLAST_FURNACE, CustomRecipeBlasting.class);
    public static final RecipeType.CookingRecipeType<CustomRecipeSmoking> SMOKER = new RecipeType.CookingRecipeType<>(Type.SMOKER, CustomRecipeSmoking.class);
    public static final RecipeType.CookingRecipeType<CustomRecipeCampfire> CAMPFIRE = new RecipeType.CookingRecipeType<>(Type.CAMPFIRE, CustomRecipeCampfire.class);
    public static final RecipeType<CustomRecipeAnvil> ANVIL = new RecipeType<>(Type.ANVIL, CustomRecipeAnvil.class);
    public static final RecipeType<CustomRecipeStonecutter> STONECUTTER = new RecipeType<>(Type.STONECUTTER, CustomRecipeStonecutter.class);
    public static final RecipeType<CustomRecipeCauldron> CAULDRON = new RecipeType<>(Type.CAULDRON, CustomRecipeCauldron.class);
    public static final RecipeType<CustomRecipeGrindstone> GRINDSTONE = new RecipeType<>(Type.GRINDSTONE, CustomRecipeGrindstone.class);
    public static final RecipeType<CustomRecipeBrewing> BREWING_STAND = new RecipeType<>(Type.BREWING_STAND, CustomRecipeBrewing.class);
    public static final RecipeType<CustomRecipeSmithing> SMITHING = new RecipeType<>(Type.SMITHING, CustomRecipeSmithing.class);

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
