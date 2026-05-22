package com.wolfyscript.customcrafting.core.recipe.modifier

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

/**
 * Modifies a target item with transformations using information about the recipe ingredients.
 */
interface RecipeItemModifier {

    companion object {
        fun of(transformations: List<Transformation>) : RecipeItemModifier = RecipeItemModifierImpl(transformations)
    }

    /**
     * The transformations that are applied to the target item.
     */
    val transformations: List<Transformation>

    /**
     * Modifies the target item using the specified transformations.
     *
     * @param target the target item to modify.
     * @param evalResult the result of the recipe evaluation (ingredient info like which ingredient is present in each slot).
     * @param context the evaluation context (e.g. player, tile entity, etc.).
     *
     * @return the modified target item.
     */
    fun modify(target: ScafallItemStack, evalResult: RecipeEvaluationResult<*, *>, context: EvaluationContext): ScafallItemStack

}