package com.wolfyscript.customcrafting.core.recipe

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wolfyscript.customcrafting.core.recipe.action.ResultAction
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.modifier.RecipeItemModifier
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import kotlin.random.Random

/**
 * The result of a recipe with modifiers and actions.
 */
@JsonDeserialize(`as` = RecipeResultImpl::class)
interface RecipeResult {

    companion object {

        /**
         * Creates a new [RecipeResult] instance with the specified choices, modifiers, and actions.
         *
         * @param choices The set of available choices for the recipe result.
         * @param modifier The modifier to apply to the resulting item.
         * @param actions The list of actions to perform when the result is collected.
         * @param bulkActions The list of actions to perform when the result is collected in bulk.
         * @param alwaysKeepPrevious Whether to keep the previous result.
         * @return A new [RecipeResult] instance.
         */
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
     * A list of actions to be performed when the recipe result is collected.
     */
    val actions: List<ResultAction>

    /**
     * The bulk actions, to perform after the recipe is completed in bulk (e.g. crafting a stack of items using shift-click).
     */
    val bulkActions: List<ResultAction>

    /**
     * Computes the result of the recipe based on the cached [RecipeEvaluationResult] and [com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext]
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