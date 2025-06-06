package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import kotlin.random.Random

/**
 * The result of a recipe with modifiers and actions.
 */
interface RecipeResult {

    val choices: RecipeChoices

    /**
     * The modifier applied to the result of the recipe.
     */
    val modifier: ResultModifier

    /**
     * The actions, to perform after the recipe is completed.
     */
    val actions: List<ResultAction>

    /**
     * The bulk actions, to perform after the recipe is completed in bulk (e.g. crafting a stack of items using shift-click).
     */
    val bulkActions: List<ResultAction>

    /**
     * Computes the result of the recipe based on the cached [RecipeData] and [EvaluationContext]
     *
     * The [random] may be used to create consistent output based on the given seed (stored on player or tile-entity).
     * A new seed is picked whenever the result is successfully collected/produced (e.g. stored in furnace result slot, picked up from inventory).
     * Therefore, when the result contains multiple items, it always picks the same item given the same seed.
     * Preventing players from rerolling the result.
     */
    fun compute(recipeData: RecipeData<*>, context: EvaluationContext, random: Random): ItemStack

    fun runActions(context: EvaluationContext, count: Int = 1)

}