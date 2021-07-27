package me.wolfyscript.customcrafting.recipes.builder;

import me.wolfyscript.customcrafting.recipes.types.AbstractShapelessCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.crafting.CraftingRecipeSettings;

public abstract class AbstractShapelessRecipeBuilder<R extends AbstractShapelessCraftingRecipe<R, S>, S extends CraftingRecipeSettings, B extends AbstractShapelessRecipeBuilder<R, S, B>> extends AbstractCraftingRecipeBuilder<R, S, B> {

    protected AbstractShapelessRecipeBuilder() {

    }

    protected AbstractShapelessRecipeBuilder(R recipe) {
        super(recipe);
    }
}
