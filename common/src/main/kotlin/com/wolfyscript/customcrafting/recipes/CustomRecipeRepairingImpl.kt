package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import kotlin.random.Random

class CustomRecipeRepairingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val process: CustomRecipeRepairing.RepairProcess,
    override val base: Ingredient,
    override val addition: Ingredient?,
) : CustomRecipeRepairing {

    override fun evaluate(
        input: RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeRepairing>? {
        val matchedBase = base.match(input.base)?.let { baseMatch ->
            IngredientDataImpl(1, 1, base, baseMatch)
        } ?: return null

        if (addition == null && input.addition != null || addition != null && input.addition == null) {
            return null
        }
        val matchedAddition = addition?.match(input.addition!!)?.let { additionMatch ->
            IngredientDataImpl(2, 2, addition, additionMatch)
        } ?: return null

        return RecipeDataImpl(this, arrayOf(matchedBase, matchedAddition))
    }

    class FixedResultImpl(override val result: RecipeResult, override val cost: Int?) :
        CustomRecipeRepairing.RepairProcess.FixedResult {

        override fun compute(
            recipeData: RecipeData<CustomRecipeRepairing>,
            context: EvaluationContext,
            random: Random,
        ): ItemStack {
            return result.compute(recipeData, context, random)
        }

    }

    class CustomDamageImpl(override val durability: Int?) : CustomRecipeRepairing.RepairProcess.CustomDamageRepair {

        override fun compute(
            recipeData: RecipeData<CustomRecipeRepairing>,
            context: EvaluationContext,
            random: Random,
        ): ItemStack {
            TODO("Not yet implemented")
        }


    }

}

