package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;

public abstract class AbstractRecipeBuilderShapeless<R extends AbstractRecipeShapeless<R, S>, S extends CraftingRecipeSettings, B extends AbstractRecipeBuilderShapeless<R, S, B>> extends AbstractCraftingRecipeBuilder<R, S, B> {

    protected AbstractRecipeBuilderShapeless() {

    }

    protected AbstractRecipeBuilderShapeless(R recipe) {
        super(recipe);
    }
}
