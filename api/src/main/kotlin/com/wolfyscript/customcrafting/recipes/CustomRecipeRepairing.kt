package com.wolfyscript.customcrafting.recipes

/**
 * Recipe used to repair items in the Anvil
 */
interface CustomRecipeRepairing : CustomRecipe {

    override val type: RecipeType<CustomRecipeRepairing>
        get() = RecipeTypes.repairing

}