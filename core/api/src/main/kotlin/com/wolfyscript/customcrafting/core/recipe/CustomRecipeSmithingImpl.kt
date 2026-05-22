package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.condition.RecipeConditions
import com.wolfyscript.customcrafting.core.recipe.condition.RecipeConditionsImpl
import com.wolfyscript.customcrafting.core.recipe.data.*
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

internal class CustomRecipeSmithingImpl(
    override val priority: Int = 0,
    override val conditions: RecipeConditions = RecipeConditionsImpl(),
    override val template: Ingredient?,
    override val base: Ingredient,
    override val addition: Ingredient?,
    override val copyOptions: CustomRecipeSmithing.CopyOptions?,
    override val result: RecipeResult,
    override val group: String = "",
) : CustomRecipeSmithing {

    override fun evaluate(
        input: RecipeInput.SmithingRecipeInput,
        context: EvaluationContext,
    ): RecipeEvaluationResult.Data? {
        if (!conditions.areSatisfied(context)) {
            return null
        }
        if (
            !validIngredient(template, input.template) ||
            (input.base == null || input.base!!.isEmpty) ||
            !validIngredient(addition, input.addition)
        ) {
            return null
        }

        val matchedTemplate = evaluateIngredient(template, input.template)
        val matchedBase = evaluateIngredient(base, input.base)
        val matchedAddition = evaluateIngredient(addition, input.addition)

        return DefaultDataImpl(arrayOf(matchedTemplate, matchedBase, matchedAddition))
    }

    private fun validIngredient(ingredient: Ingredient?, inputStack: ScafallItemStack?): Boolean {
        val emptyStack = inputStack == null || inputStack.isEmpty
        if (ingredient == null) {
            return emptyStack
        }
        return !emptyStack
    }

    private fun evaluateIngredient(ingredient: Ingredient?, inputStack: ScafallItemStack?): IngredientData? {
        if (ingredient == null || inputStack == null || inputStack.isEmpty) {
            return null
        }
        return ingredient.match(inputStack)?.let { ingredientMatch ->
            IngredientDataImpl(0, 0, ingredient, ingredientMatch)
        }
    }

    override fun toString(): String {
        return "smithing ($priority), template=$template, base=$base, addition=$addition, copying $copyOptions, producing $result if $conditions"
    }

    data class CopyOptionsImpl(
        override val preserveComponents: List<Key> = emptyList(),
        override val excludeComponents: List<Key> = emptyList(),
    ) : CustomRecipeSmithing.CopyOptions {

        override fun toString(): String {
            return "(preserve $preserveComponents, exclude $excludeComponents)"
        }
    }

}

