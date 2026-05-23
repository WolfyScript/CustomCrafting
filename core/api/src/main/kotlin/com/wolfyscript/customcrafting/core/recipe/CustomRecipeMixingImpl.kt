package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.condition.RecipeConditions
import com.wolfyscript.customcrafting.core.recipe.condition.RecipeConditionsImpl
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient

internal class CustomRecipeMixingImpl(
    override val priority: Int = 0,
    override val conditions: RecipeConditions = RecipeConditionsImpl(),
    override val processingTime: Int,
    override val xp: Int,
    override val results: List<RecipeResult>,
    override val ingredients: List<Ingredient>,
    override val fluidRequirement: CustomRecipeMixing.FluidRequirement?,
    override val campfireRequirement: CustomRecipeMixing.CampfireRequirement?,
    override val group: String = ""
) : CustomRecipeMixing {

    override fun evaluate(
        input: RecipeInput.MixingRecipeInput,
        context: EvaluationContext
    ): RecipeEvaluationResult.Data? {
        TODO("Not yet implemented")
    }

    data class FluidRequirementImpl(
        override val lava: Boolean,
        override val water: Boolean,
        override val level: Int
    ) : CustomRecipeMixing.FluidRequirement

    data class CampfireRequirementImpl(
        override val soulCampfire: Boolean,
        override val normalCampfire: Boolean,
        override val signalFire: Boolean
    ) : CustomRecipeMixing.CampfireRequirement

}