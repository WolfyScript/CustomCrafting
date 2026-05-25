package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient
import com.wolfyscript.customcrafting.core.recipe.process.ProcessGrinding

/**
 * Represents a custom grinding recipe.
 */
interface CustomRecipeGrinding : CustomRecipe<RecipeInput.GrindingRecipeInput, RecipeEvaluationResult.GrindingRecipeData> {

    /**
     * The type of this recipe.
     * Returns the [RecipeType] for grinding recipes.
     */
    override val type: RecipeType<CustomRecipeGrinding>
        get() = RecipeTypes.grinding.resolveOrThrow()

    /**
     * The base ingredient for this grinding recipe.
     */
    val base: Ingredient

    /**
     * An optional additional ingredient for this grinding recipe.
     *
     * This property can be null if no additional ingredient is required.
     */
    val addition: Ingredient?

    /**
     * The grinding process for this recipe.
     */
    val process: ProcessGrinding

}