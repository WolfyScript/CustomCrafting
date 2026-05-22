package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.action.ResultAction
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.modifier.RecipeItemModifier
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import kotlin.random.Random

/**
 * The result of a recipe with modifiers and actions.
 */
interface RecipeResult {

    companion object {

        fun of(
            choices: RecipeChoices,
            modifier: RecipeItemModifier,
            actions: List<ResultAction> = emptyList(),
            bulkActions: List<ResultAction> = emptyList(),
            alwaysKeepPrevious: Boolean = false,
        ): RecipeResult =
            RecipeResultImpl(
                choices, modifier, actions, bulkActions, alwaysKeepPrevious
            )

    }

    /**
     * The items from which the result is chosen upon processing it.
     * The result is picked randomly from the choices using a seed.
     */
    val choices: RecipeChoices

    /**
     * Whether it should always produce the same stack when not yet collected.
     *
     * If enabled, the seed used to pick the stack is stored on the player or tile-entity that processed the recipe.
     * The seed is reset when the result stack is collected (or produced by tile-entities).
     * (Does not persist across server restarts)
     *
     * This prevents rerolling the result stack without collecting it.
     */
    val alwaysKeepPrevious: Boolean

    /**
     * The modifier applied to the result of the recipe.
     */
    val modifier: RecipeItemModifier

    /**
     * The actions, to perform after the recipe is completed.
     */
    val actions: List<ResultAction>

    /**
     * The bulk actions, to perform after the recipe is completed in bulk (e.g. crafting a stack of items using shift-click).
     */
    val bulkActions: List<ResultAction>

    /**
     * Computes the result of the recipe based on the cached [com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult] and [com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext]
     *
     * The [random] may be used to create consistent output based on the given seed (stored on player or tile-entity).
     * A new seed is picked whenever the result is successfully collected/produced (e.g. stored in furnace result slot, picked up from inventory).
     * Therefore, when the result contains multiple items, it always picks the same item given the same seed.
     * Preventing players from rerolling the result.
     */
    fun compute(
        recipeEvaluationResult: RecipeEvaluationResult<*, *>,
        context: EvaluationContext,
        random: Random,
    ): ScafallItemStack

    /**
     * Runs the specified actions in the given [EvaluationContext].
     *
     * @param count the number of times to run the actions.
     * @param context the evaluation context (e.g. player, tile entity, etc.).
     */
    fun runActions(context: EvaluationContext, count: Int = 1)

}