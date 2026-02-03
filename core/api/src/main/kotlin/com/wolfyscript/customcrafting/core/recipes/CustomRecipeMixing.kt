package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient

/**
 * Recipe used to mix items in the Cauldron
 */
interface CustomRecipeMixing : CustomRecipe<com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.MixingRecipeInput, com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult.Data> {

    override val type: RecipeType<CustomRecipeMixing>
        get() = RecipeTypes.mixing.resolveOrThrow()

    val processingTime: Int

    val xp: Int

    val results: List<RecipeResult>

    val ingredients: List<com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient>

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