package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

/**
 * Recipe used to mix items in the Cauldron
 */
interface CustomRecipeMixing : CustomRecipe<CustomRecipeMixing> {

    val processingTime: Int

    val xp: Int

    val results: List<RecipeResult>

    val ingredients: List<Ingredient>

    val fluidRequirement: FluidRequirement?

    val campfireRequirement: CampfireRequirement?

    fun evaluate(context: EvaluationContext, stacks: List<ItemStack?>): RecipeData<CustomRecipeMixing>?

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