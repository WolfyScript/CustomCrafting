package com.wolfyscript.customcrafting.recipes.repair

import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing
import com.wolfyscript.customcrafting.recipes.EvaluationContext
import com.wolfyscript.customcrafting.recipes.RecipeResult
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import kotlin.random.Random

class FixedResultImpl(
    override val result: RecipeResult,
    override val cost: Int?,
    override val rename: RenameOptions? = null,
) :
    CombineProcess.FixedResult {

    override fun compute(
        recipeEvaluationResult: RecipeEvaluationResult<RecipeEvaluationResult.RepairingRecipeData, CustomRecipeRepairing>,
        input: RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
        random: Random,
    ): ItemStack {

        val resultStack = result.compute(recipeEvaluationResult, context, random)

        if (rename != null) {
            // TODO
        }

        return resultStack
    }

}