package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.core.recipes.process.ProcessGrinding

interface CustomRecipeGrinding : CustomRecipe<com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.GrindingRecipeInput, com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult.GrindingRecipeData> {

    override val type: RecipeType<CustomRecipeGrinding>
        get() = RecipeTypes.grinding.resolveOrThrow()

    val base: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient

    val addition: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient?

    val process: com.wolfyscript.customcrafting.core.recipes.process.ProcessGrinding

}