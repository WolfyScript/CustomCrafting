package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import kotlin.random.Random

/**
 * Recipe used to repair items in the Anvil
 */
interface CustomRecipeRepairing : CustomRecipe<RecipeInput.RepairingRecipeInput, CustomRecipeRepairing> {

    override val type: RecipeType<CustomRecipeRepairing>
        get() = RecipeTypes.repairing.resolveOrThrow()

    val process: RepairProcess

    val base: Ingredient

    val addition: Ingredient?

    override fun evaluate(
        input: RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
    ): RecipeData.RepairingRecipeData?

    /**
     * The process in which the repairing is done.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonPropertyOrder(value = ["type"])
    sealed interface RepairProcess {

        /**
         * Computes the result based on the data and context.
         *
         * The [random] may be used to create consistent output based on the players stored repairing seed.
         * A new seed is picked whenever the player successfully collects the result from the inventory.
         * Therefore, when the result contains multiple items, it always picks the same item given the same seed.
         * Preventing players from rerolling the result.
         */
        fun compute(recipeData: RecipeData.RepairingRecipeData, input: RecipeInput.RepairingRecipeInput, context: EvaluationContext, random: Random): ItemStack

        /**
         * Always uses the specified result and computes the resulting stack based on the data and context.
         */
        @JsonTypeName("fixed_result")
        interface FixedResult : RepairProcess {

            val result: RecipeResult

            val cost: Int?

        }

        /**
         * Uses the vanilla behaviour but with optional additional properties to manipulate e.g. extra durability applied to the resulting stack.
         */
        @JsonTypeName("custom_damage_repair")
        interface CustomDamageRepair : RepairProcess {

            val additionalRepair: Int?

            /**
             * Whether the durability of the base item and additional item (if it is damageable) should be combined
             * in percentages instead of combining the actual durability values (like in vanilla).
             *
             * This means it is independent of the max damage difference between the items. An addition with vastly
             * more max damage won't benefit the repair process.
             */
            val combineDurabilityAsRatio: Boolean

        }


    }

}