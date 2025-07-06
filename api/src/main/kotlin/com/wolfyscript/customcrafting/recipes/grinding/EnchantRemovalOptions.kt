package com.wolfyscript.customcrafting.recipes.grinding

import com.wolfyscript.scafall.identifier.Key

interface EnchantRemovalOptions {

    /**
     * How enchantments should be removed from the base ingredient (top slot)
     *
     * If omitted, all enchantments are kept.
     */
    val baseEnchants: IngredientEnchantRemovalOptions?

    /**
     * How enchantments should be removed from the addition ingredient (bottom slot)
     *
     * If omitted, all enchantments are kept.
     */
    val additionEnchants: IngredientEnchantRemovalOptions?

    interface IngredientEnchantRemovalOptions {

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
         * If omitted, all enchantments are kept.
         */
        val enchants: EnchantmentList?

        /**
         * A list of Enchantments marked either for removal or keeping
         */
        interface EnchantmentList {

            val enchantments: List<Key>

            /**
             * Removes the specified enchantments
             */
            interface RemoveEnchants : EnchantmentList

            /**
             * Keeps the specified enchantments
             */
            interface KeepEnchants : EnchantmentList

        }

    }

}