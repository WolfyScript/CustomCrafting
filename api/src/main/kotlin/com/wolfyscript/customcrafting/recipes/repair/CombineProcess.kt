package com.wolfyscript.customcrafting.recipes.repair

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing
import com.wolfyscript.customcrafting.recipes.EvaluationContext
import com.wolfyscript.customcrafting.recipes.RecipeResult
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import kotlin.random.Random

/**
 * The process in which a [com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing] produces the result in an Anvil menu.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonPropertyOrder(value = ["type"])
sealed interface CombineProcess {

    /**
     * Computes the result based on the data and context.
     *
     * The [random] may be used to create consistent output based on the players stored repairing seed.
     * A new seed is picked whenever the player successfully collects the result from the inventory.
     * Therefore, when the result contains multiple items, it always picks the same item given the same seed.
     * Preventing players from rerolling the result.
     */
    fun compute(
        recipeEvaluationResult: RecipeEvaluationResult<RecipeEvaluationResult.RepairingRecipeData, CustomRecipeRepairing>,
        input: RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
        random: Random,
    ): ItemStack

    /**
     * Always uses the specified result and computes the resulting stack based on the data and context.
     */
    @JsonTypeName("fixed_result")
    interface FixedResult : CombineProcess {

        /**
         * Defines how the result should be renamed.
         *
         * Optional: when omitted, the name is not applied
         */
        val rename: RenameOptions?

        val result: RecipeResult

        val cost: Int?

    }

    /**
     * Tries to mirror the vanilla logic of the anvil as much as possible, while providing lots of customization options.
     */
    @JsonTypeName("custom")
    interface CustomCombineProcess : CombineProcess {

        /**
         * Defines how the result should be renamed.
         *
         * Optional: when omitted, the name is not applied
         */
        val rename: RenameOptions?

        /**
         * Defines how the base is repaired when both base and addition are damageable.
         *
         * Optional: when omitted, the base is not repaired.
         */
        val damageCombine: DamageCombineOptions?

        /**
         * Defines how the base is repaired when the addition is a non-damageable item.
         *
         * Optional: when omitted, the base is not repaired.
         */
        val itemRepair: ItemRepairOptions?

        /**
         * Defines how the enchantments from the base and addition are combined.
         *
         * Optional: when omitted, the enchantments of the addition are ignored.
         */
        val enchanting: EnchantingOptions?

    }


}