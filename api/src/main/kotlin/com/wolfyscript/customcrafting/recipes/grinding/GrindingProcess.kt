package com.wolfyscript.customcrafting.recipes.grinding

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.wolfyscript.customcrafting.recipes.CustomRecipeGrinding
import com.wolfyscript.customcrafting.recipes.EvaluationContext
import com.wolfyscript.customcrafting.recipes.RecipeResult
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.repair.DamageCombineOptions
import com.wolfyscript.customcrafting.recipes.repair.EnchantingOptions
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import kotlin.random.Random

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonPropertyOrder(value = ["type"])
sealed interface GrindingProcess {

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
    ): ItemStack

    @JsonTypeName("fixed_result")
    interface FixedResultGrindingProcess : GrindingProcess {

        val result: RecipeResult

        val xp: Int

    }

    @JsonTypeName("default")
    interface DefaultGrindingProcess : GrindingProcess{

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
        val removeEnchants: EnchantRemovalOptions

        /**
         * Specifies how enchants, that are not removed, are merged together.
         *
         * By default, all enchantments that were not removed are merged.
         */
        val mergeEnchants: EnchantingOptions

        /**
         * Specifies how the durability of both ingredients (if there are two) is combined.
         *
         * By default, it combines the durability of the base and addition ingredient and adds a 5% bonus of the base max damage.
         */
        val damageCombine: DamageCombineOptions

        /**
         * Specifies how the grindstone applies the repair cost to the result.
         *
         * If omitted, no repair cost will get applied.
         */
        val repairCost: RepairCostOptions?

    }

}