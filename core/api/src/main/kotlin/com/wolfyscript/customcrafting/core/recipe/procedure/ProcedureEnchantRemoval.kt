package com.wolfyscript.customcrafting.core.recipe.procedure

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(`as` = ProcedureEnchantRemovalImpl::class)
interface ProcedureEnchantRemoval {

    /**
     * How enchantments should be removed from the base ingredient (top slot)
     *
     * By default, all enchantments, except curses are removed.
     */
    val baseEnchants: IngredientEnchantRemovalProcedure

    /**
     * How enchantments should be removed from the addition ingredient (bottom slot)
     *
     * By default, all enchantments, except curses are removed.
     */
    val additionEnchants: IngredientEnchantRemovalProcedure

    @JsonDeserialize(`as` = ProcedureEnchantRemovalIngredientImpl::class)
    interface IngredientEnchantRemovalProcedure {

        /**
         * Whether the curses should be removed or not.
         *
         * Hint: curses are also just enchantments, so if a specific curse should be kept or removed see [enchants]
         *
         * Default: false
         */
        val removeCurses: Boolean

        /**
         * Enchantments that should be removed or kept.
         *
         * By default, empty so all the enchantments, except curses, are removed.
         */
        val enchants: List<Key>

        /**
         * Whether the [enchants] should be kept or removed from the ingredient.
         *
         * By default, they are kept
         */
        val type: SetInclusionExclusionType

        fun removeFrom(stack: ScafallItemStack) : Int

    }

}

