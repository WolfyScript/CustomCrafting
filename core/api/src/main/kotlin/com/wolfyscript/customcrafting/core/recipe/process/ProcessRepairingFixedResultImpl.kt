package com.wolfyscript.customcrafting.core.recipe.process

import com.wolfyscript.customcrafting.core.recipe.CustomRecipeRepairing
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.RecipeResult
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.procedure.ProcedureRename
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import kotlin.random.Random

internal class ProcessRepairingFixedResultImpl(
    override val result: RecipeResult,
    override val cost: Int?,
    override val rename: ProcedureRename? = null,
) :
    ProcessRepairing.FixedResult {

    override fun compute(
        recipeEvaluationResult: RecipeEvaluationResult<RecipeEvaluationResult.RepairingRecipeData, CustomRecipeRepairing>,
        input: RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
        random: Random,
    ): ScafallItemStack {

        val resultStack = result.compute(recipeEvaluationResult, context, random)

        if (rename != null) {
            // TODO
        }

        return resultStack
    }

}