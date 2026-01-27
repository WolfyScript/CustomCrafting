package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.recipes.process.ProcessGrinding

interface CustomRecipeGrinding : CustomRecipe<RecipeInput.GrindingRecipeInput, RecipeEvaluationResult.GrindingRecipeData> {

    override val type: RecipeType<CustomRecipeGrinding>
        get() = RecipeTypes.grinding.resolveOrThrow()

    val base: Ingredient

    val addition: Ingredient?

    val process: ProcessGrinding

}