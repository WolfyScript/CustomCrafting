package com.wolfyscript.customcrafting.fabric.inject;

public interface RecipeManagerCustomRecipesExt {

    /**
     * Register custom recipes as proxy recipes that integrate with the vanilla recipe system.
     * This includes recipe types that have a vanilla counterpart.
     * Others like Grindstone recipes are not registered as proxy recipes.
     */
    void registerProxyRecipes();

}
