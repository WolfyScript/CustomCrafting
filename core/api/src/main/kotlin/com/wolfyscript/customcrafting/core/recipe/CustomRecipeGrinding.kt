package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient
import com.wolfyscript.customcrafting.core.recipe.process.ProcessGrinding

interface CustomRecipeGrinding : CustomRecipe<RecipeInput.GrindingRecipeInput, RecipeEvaluationResult.GrindingRecipeData> {

    override val type: RecipeType<CustomRecipeGrinding>
        get() = RecipeTypes.grinding.resolveOrThrow()

    val base: Ingredient

    val addition: Ingredient?

    val process: ProcessGrinding

}