package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.condition.RecipeConditions
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

/**
 * A custom recipe for the player inventory crafting grid, crafting table, and auto-crafter.
 *
 * The [formula] defines how the recipe is evaluated. (Shapeless or Shaped)
 *
 */
interface CustomRecipeCrafting : CustomRecipe<RecipeInput.CraftingRecipeInput, RecipeEvaluationResult.Data> {

    companion object {

        fun of(
            group: String, priority: Int, conditions: RecipeConditions, formula: CraftingFormula, result: RecipeResult,
        ): CustomRecipeCrafting {
            return CustomRecipeCraftingImpl(priority, conditions, formula, result)
        }

    }

    override val type: RecipeType<CustomRecipeCrafting>
        get() = RecipeTypes.crafting.resolveOrThrow()

    /**
     * The formula used to evaluate the recipe.
     *
     * For example, a shapeless recipe would use [CraftingFormula.Shapeless]
     * and a shaped recipe would use [CraftingFormula.Shaped]
     */
    val formula: CraftingFormula

    /**
     * The result of the recipe.
     */
    val result: RecipeResult

    /**
     * Shrinks the given matrix by the given count (if possible).
     *
     * @param applyStacks A function that is called for each stack in the matrix that is shrunk.
     */
    fun shrink(
        input: RecipeInput.CraftingRecipeInput,
        recipeEvaluationResult: RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting>,
        context: EvaluationContext,
        count: Int,
        applyStacks: (index: Int, new: ScafallItemStack) -> Unit,
    )

}

