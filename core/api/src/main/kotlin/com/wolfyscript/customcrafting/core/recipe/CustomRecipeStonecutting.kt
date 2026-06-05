package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient

interface CustomRecipeStonecutting : CustomRecipe<RecipeInput.SingleSlotRecipeInput, RecipeEvaluationResult.Data> {

    override val type: RecipeType<CustomRecipeStonecutting>
        get() = RecipeTypes.stonecutting.resolveOrThrow()

    /**
     * The input ingredient required for this stonecutting recipe.
     */
    val source: Ingredient

    /**
     * The result produced by this stonecutting recipe, encompassing output choices, modifiers, and actions.
     */
    val result: RecipeResult

    /**
     * Creates a proxy recipe (vanilla recipe) for each result item.
     * (basically separating it into multiple stonecutter buttons)
     *
     * Actions and other Result settings apply to all of those recipes.
     */
    val flattenResult: Boolean

}