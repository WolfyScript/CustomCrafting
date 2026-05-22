package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient

/**
 * Recipe used to mix items in the Cauldron
 */
interface CustomRecipeMixing : CustomRecipe<RecipeInput.MixingRecipeInput, RecipeEvaluationResult.Data> {

    override val type: RecipeType<CustomRecipeMixing>
        get() = RecipeTypes.mixing.resolveOrThrow()

    val processingTime: Int

    val xp: Int

    val results: List<RecipeResult>

    val ingredients: List<Ingredient>

    val fluidRequirement: FluidRequirement?

    val campfireRequirement: CampfireRequirement?

    interface FluidRequirement {

        val lava: Boolean

        val water: Boolean

        val level: Int

    }

    interface CampfireRequirement {

        val soulCampfire: Boolean

        val normalCampfire: Boolean

        val signalFire: Boolean

    }

}