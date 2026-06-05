package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient

/**
 * Recipe used to mix items in the Cauldron
 */
@JsonDeserialize(`as` = CustomRecipeMixingImpl::class)
interface CustomRecipeMixing : CustomRecipe<RecipeInput.MixingRecipeInput, RecipeEvaluationResult.Data> {

    override val type: RecipeType<CustomRecipeMixing>
        get() = RecipeTypes.mixing.resolveOrThrow()

    val processingTime: Int

    val xp: Int

    val results: List<RecipeResult>

    val ingredients: List<Ingredient>

    val fluidRequirement: FluidRequirement?

    val campfireRequirement: CampfireRequirement?

    @JsonDeserialize(`as` = CustomRecipeMixingImpl.FluidRequirementImpl::class)
    interface FluidRequirement {

        val lava: Boolean

        val water: Boolean

        val level: Int

    }

    @JsonDeserialize(`as` = CustomRecipeMixingImpl.CampfireRequirementImpl::class)
    interface CampfireRequirement {

        val soulCampfire: Boolean

        val normalCampfire: Boolean

        val signalFire: Boolean

    }

}