package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient

interface CustomRecipeStonecutting : CustomRecipe<com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.SingleSlotRecipeInput, com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult.Data> {

    override val type: RecipeType<CustomRecipeStonecutting>
        get() = RecipeTypes.stonecutting.resolveOrThrow()

    val source: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient

    val result: RecipeResult

    /**
     * Creates a proxy recipe (vanilla recipe) for each result item.
     * (basically separating it into multiple stonecutter buttons)
     *
     * Actions and other Result settings apply to all of those recipes.
     */
    val flattenResult: Boolean

}