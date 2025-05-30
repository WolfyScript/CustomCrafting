package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.identifier.Key

class CustomRecipeSmithingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val template: Ingredient?,
    override val base: Ingredient?,
    override val addition: Ingredient?,
    override val copyOptions: CustomRecipeSmithing.CopyOptions?,
    override val result: RecipeResult,
) : CustomRecipeSmithing {

    override fun evaluate(
        input: RecipeInput.SmithingRecipeInput,
        context: EvaluationContext
    ): RecipeData<CustomRecipeSmithing>? {
        if (!conditions.areSatisfied(context)) {
            return null
        }

        if (template == null && input.template != null || template != null && input.template == null) {
            return null
        }
        val matchedTemplate = template?.let {
            it.match(input.template!!, true)?.let { templateMatch ->
                IngredientDataImpl(0, 0, template, templateMatch)
            } ?: return null
        }

        if (base == null && input.base != null || base != null && input.base == null) {
            return null
        }
        val matchedBase = base?.let {
            it.match(input.base!!, true)?.let { baseMatch ->
                IngredientDataImpl(1, 1, base, baseMatch)
            } ?: return null
        }

        if (addition == null && input.addition != null || addition != null && input.addition == null) {
            return null
        }
        val matchedAddition = addition?.let {
            it.match(input.addition!!, true)?.let { additionMatch ->
                IngredientDataImpl(2, 2, addition, additionMatch)
            } ?: return null
        }

        return RecipeDataImpl(this, result, arrayOf(matchedTemplate, matchedBase, matchedAddition))
    }

    data class CopyOptionsImpl(override val preserveComponents: List<Key>) : CustomRecipeSmithing.CopyOptions

}