package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput

class CustomRecipeGrindingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val xp: Int = 0,
    override val ingredients: List<Ingredient>,
    override val result: RecipeResult,
) : CustomRecipeGrinding {

    override fun evaluate(
        input: RecipeInput.GrindingRecipeInput,
        context: EvaluationContext
    ): RecipeData<CustomRecipeGrinding>? {
        TODO("Not yet implemented")
    }

}