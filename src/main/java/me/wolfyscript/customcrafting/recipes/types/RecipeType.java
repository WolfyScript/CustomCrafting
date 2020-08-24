package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.smithing.CustomSmithingRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingRecipe;

import java.util.Locale;

public enum RecipeType {

    WORKBENCH(CraftingRecipe.class),
    ELITE_WORKBENCH(EliteCraftingRecipe.class),
    ANVIL(CustomAnvilRecipe.class),
    FURNACE(CustomFurnaceRecipe.class, "cooking"),
    BLAST_FURNACE(CustomBlastRecipe.class, "cooking"),
    SMOKER(CustomSmokerRecipe.class, "cooking"),
    CAMPFIRE(CustomCampfireRecipe.class, "cooking"),
    STONECUTTER(CustomStonecutterRecipe.class),
    CAULDRON(CauldronRecipe.class),
    GRINDSTONE(GrindstoneRecipe.class),
    BREWING_STAND(BrewingRecipe.class),
    SMITHING(CustomSmithingRecipe.class);

    private final String id;
    private final String creatorID;
    private final Class<? extends CustomRecipe> recipeClass;

    RecipeType(Class<? extends CustomRecipe> recipeClass) {
        this.id = this.toString().toLowerCase(Locale.ROOT);
        this.creatorID = this.id;
        this.recipeClass = recipeClass;
    }

    RecipeType(Class<? extends CustomRecipe> recipeClass, String creatorID) {
        this.id = this.toString().toLowerCase(Locale.ROOT);
        this.creatorID = creatorID;
        this.recipeClass = recipeClass;
    }

    public String getId() {
        return id;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public Class<? extends CustomRecipe> getRecipeClass() {
        return recipeClass;
    }
}
