package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.recipes.RecipeReference;

import java.util.Collection;

public interface RecipeManagerCustomRecipesExt {

    /**
     * Register custom recipes as proxy recipes.
     * Not all custom recipes can be registered as proxy recipes, but those that have a vanilla counterpart can.
     */
    void registerProxyRecipes();

}
