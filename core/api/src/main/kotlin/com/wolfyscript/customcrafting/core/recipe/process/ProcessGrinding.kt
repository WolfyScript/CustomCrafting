package com.wolfyscript.customcrafting.core.recipe.process

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.wolfyscript.customcrafting.core.recipe.CustomRecipeGrinding
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.RecipeResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.procedure.ProcedureDamageCombine
import com.wolfyscript.customcrafting.core.recipe.procedure.ProcedureEnchantRemoval
import com.wolfyscript.customcrafting.core.recipe.procedure.ProcedureEnchanting
import com.wolfyscript.customcrafting.core.recipe.procedure.ProcedureRepairCost
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import kotlin.random.Random

/**
 * The process in which [com.wolfyscript.customcrafting.core.recipe.CustomRecipeGrinding] computes the result based on the defined procedures.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonPropertyOrder(value = ["type"])
sealed interface ProcessGrinding {

    /**
     * Computes the result based on the data and context.
     *
     * The [random] may be used to create consistent output based on the players stored seed.
     * A new seed is picked whenever the player successfully collects the result from the inventory.
     * Therefore, when the result contains multiple items, it always picks the same item given the same seed.
     * Preventing players from rerolling the result.
     */
    fun compute(
        recipeEvaluationResult: RecipeEvaluationResult<RecipeEvaluationResult.GrindingRecipeData, CustomRecipeGrinding>,
        input: RecipeInput.GrindingRecipeInput,
        context: EvaluationContext,
        random: Random,
    ): ScafallItemStack

    /**
     * A process that always returns the specified [result] and [xp].
     */
    @JsonTypeName("fixed_result")
    interface FixedResultProcessGrinding : ProcessGrinding {

        val result: RecipeResult

        val xp: Int

    }

    @JsonTypeName("default")
    interface DefaultProcessGrinding : ProcessGrinding{

        /**
         * The extra amount of experience to drop.
         * This gets added to the experience calculated from the process depending on the options enabled.
         */
        val extraXp: Int

        /**
         * Specifies how enchants are removed from both ingredients.
         *
         * By default, all enchantments, except curses, are removed.
         */
        val removeEnchants: ProcedureEnchantRemoval

        /**
         * Specifies how enchants, that are not removed, are merged together.
         *
         * By default, all enchantments that were not removed are merged.
         */
        val mergeEnchants: ProcedureEnchanting

        /**
         * Specifies how the durability of both ingredients (if there are two) is combined.
         *
         * By default, it combines the durability of the base and addition ingredient and adds a 5% bonus of the base max damage.
         */
        val damageCombine: ProcedureDamageCombine

        /**
         * Specifies how the grindstone applies the repair cost to the result.
         *
         * If omitted, no repair cost will get applied.
         */
        val repairCost: ProcedureRepairCost?

    }

}