package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient

class CustomRecipeMixingImpl(
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