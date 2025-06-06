package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import kotlin.random.Random

/**
 * Recipe used to repair items in the Anvil
 */
interface CustomRecipeRepairing : CustomRecipe<RecipeInput.RepairingRecipeInput, CustomRecipeRepairing> {

    override val type: RecipeType<CustomRecipeRepairing>
        get() = RecipeTypes.repairing

    val process: RepairProcess

    /**
     * The process in which the repairing is done.
     */
    interface RepairProcess {

        /**
         * Computes the result based on the data and context.
         *
         * The [random] may be used to create consistent output based on the players stored repairing seed.
         * A new seed is picked whenever the player successfully collects the result from the inventory.
         * Therefore, when the result contains multiple items, it always picks the same item given the same seed.
         * Preventing players from rerolling the result.
         */
        fun compute(recipeData: RecipeData<CustomRecipeRepairing>, context: EvaluationContext, random: Random): ItemStack

        /**
         * Always uses the specified result and computes the resulting stack based on the data and context.
         */
        interface FixedResult : RepairProcess {

            val result: RecipeResult

            val cost: Int?

        }

        /**
         * Uses the vanilla behaviour but with optional additional properties to manipulate e.g. extra durability applied to the resulting stack.
         */
        interface CustomDamageRepair : RepairProcess {

            val durability: Int?

        }


    }

}