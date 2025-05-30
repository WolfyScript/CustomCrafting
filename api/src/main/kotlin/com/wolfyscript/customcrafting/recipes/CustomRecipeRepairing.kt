package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeInput

/**
 * Recipe used to repair items in the Anvil
 */
interface CustomRecipeRepairing : CustomRecipe<RecipeInput.RepairingRecipeInput, CustomRecipeRepairing> {

    override val type: RecipeType<CustomRecipeRepairing>
        get() = RecipeTypes.repairing

}