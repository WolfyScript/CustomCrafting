package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput

class CustomRecipeRepairingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
) : CustomRecipeRepairing {

    override fun evaluate(
        input: RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeRepairing>? {
        TODO("Not yet implemented")
    }

}