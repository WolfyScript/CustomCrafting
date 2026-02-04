package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.core.recipes.process.ProcessGrinding

interface CustomRecipeGrinding : CustomRecipe<RecipeInput.GrindingRecipeInput, RecipeEvaluationResult.GrindingRecipeData> {

    override val type: RecipeType<CustomRecipeGrinding>
        get() = RecipeTypes.grinding.resolveOrThrow()

    val base: Ingredient

    val addition: Ingredient?

    val process: ProcessGrinding

}