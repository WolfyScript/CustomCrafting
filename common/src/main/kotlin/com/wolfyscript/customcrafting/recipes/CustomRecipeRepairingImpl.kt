package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import kotlin.random.Random

class CustomRecipeRepairingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val process: CustomRecipeRepairing.RepairProcess,
) : CustomRecipeRepairing {

    override fun evaluate(
        input: RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeRepairing>? {
        TODO("Not yet implemented")
    }

    class FixedResultImpl(override val result: RecipeResult, override val cost: Int?) : CustomRecipeRepairing.RepairProcess.FixedResult {

        override fun compute(
            recipeData: RecipeData<CustomRecipeRepairing>,
            context: EvaluationContext,
            random: Random
        ): ItemStack {
            return result.compute(recipeData, context, random)
        }

    }

    class CustomDamageImpl(override val durability: Int?) : CustomRecipeRepairing.RepairProcess.CustomDamageRepair {

        override fun compute(
            recipeData: RecipeData<CustomRecipeRepairing>,
            context: EvaluationContext,
            random: Random
        ): ItemStack {
            TODO("Not yet implemented")
        }


    }

}

