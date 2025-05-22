package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.IngredientData
import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class CustomRecipeSmithingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val template: Ingredient?,
    override val base: Ingredient?,
    override val addition: Ingredient?,
    override val copyOptions: CustomRecipeSmithing.CopyOptions?,
    override val result: RecipeResult,
) : CustomRecipeSmithing {

    override val type: RecipeType<CustomRecipeSmithing>
        get() = TODO("Not yet implemented")

    override fun evaluate(
        context: EvaluationContext,
        templateStack: ItemStack?,
        baseStack: ItemStack?,
        additionStack: ItemStack?,
    ): RecipeData<CustomRecipeSmithing>? {
        if (!conditions.areSatisfied(context)) {
            return null
        }

        if (template == null && templateStack != null || template != null && templateStack == null) {
            return null
        }
        val matchedTemplate = template?.let {
            it.match(templateStack!!, true)?.let { templateMatch ->
                IngredientDataImpl(0, 0, template, templateMatch)
            } ?: return null
        }

        if (base == null && baseStack != null || base != null && baseStack == null) {
            return null
        }
        val matchedBase = base?.let {
            it.match(baseStack!!, true)?.let { baseMatch ->
                IngredientDataImpl(1, 1, base, baseMatch)
            } ?: return null
        }

        if (addition == null && additionStack != null || addition != null && additionStack == null) {
            return null
        }
        val matchedAddition = addition?.let {
            it.match(additionStack!!, true)?.let { additionMatch ->
                IngredientDataImpl(2, 2, addition, additionMatch)
            } ?: return null
        }

        return RecipeDataImpl(this, result, arrayOf(matchedTemplate, matchedBase, matchedAddition))
    }

    data class CopyOptionsImpl(override val preserveComponents: List<Key>) : CustomRecipeSmithing.CopyOptions

}