package com.wolfyscript.customcrafting.core.recipe.process

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.wolfyscript.customcrafting.core.recipe.CustomRecipeRepairing
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.RecipeResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.procedure.ProcedureDamageCombine
import com.wolfyscript.customcrafting.core.recipe.procedure.ProcedureEnchanting
import com.wolfyscript.customcrafting.core.recipe.procedure.ProcedureItemRepair
import com.wolfyscript.customcrafting.core.recipe.procedure.ProcedureRename
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import kotlin.random.Random

/**
 * The process in which a [com.wolfyscript.customcrafting.core.recipe.CustomRecipeRepairing] produces the result in an Anvil menu.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonPropertyOrder(value = ["type"])
sealed interface ProcessRepairing {

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
    ): ScafallItemStack

    /**
     * Always uses the specified result and computes the resulting stack based on the data and context.
     */
    @JsonTypeName("fixed_result")
    interface FixedResult : ProcessRepairing {

        /**
         * Defines how the result should be renamed.
         *
         * Optional: when omitted, the name is not applied
         */
        val rename: ProcedureRename?

        val result: RecipeResult

        val cost: Int?

    }

    /**
     * Tries to mirror the vanilla logic of the anvil as much as possible, while providing lots of customization options.
     */
    @JsonTypeName("custom")
    interface CustomProcessRepairing : ProcessRepairing {

        /**
         * Defines how the result should be renamed.
         *
         * Optional: when omitted, the name is not applied
         */
        val rename: ProcedureRename?

        /**
         * Defines how the base is repaired when both base and addition are damageable.
         *
         * Optional: when omitted, the base is not repaired.
         */
        val damageCombine: ProcedureDamageCombine?

        /**
         * Defines how the base is repaired when the addition is a non-damageable item.
         *
         * Optional: when omitted, the base is not repaired.
         */
        val itemRepair: ProcedureItemRepair?

        /**
         * Defines how the enchantments from the base and addition are combined.
         *
         * Optional: when omitted, the enchantments of the addition are ignored.
         */
        val enchanting: ProcedureEnchanting?

    }


}