package com.wolfyscript.customcrafting.recipes.grinding

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.wolfyscript.customcrafting.recipes.RecipeResult
import com.wolfyscript.customcrafting.recipes.repair.DamageCombineOptions

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonPropertyOrder(value = ["type"])
sealed interface GrindingProcess {

    @JsonTypeName("fixed_result")
    interface FixedResultGrindingProcess {

        val result: RecipeResult

        val xp: Int

    }

    @JsonTypeName("default")
    interface DefaultGrindingProcess {

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
        val mergeEnchants: EnchantMergeOptions

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