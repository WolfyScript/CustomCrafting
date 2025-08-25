package com.wolfyscript.customcrafting.recipes.process

import com.wolfyscript.scafall.wrappers.ScafallPlayer
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

/**
 * Specifies how enchantments from an additional item are merged onto a result item.
 * The result item must be able to store enchantments.
 *
 * This applies to almost all scenarios that involve enchantments in a way, like:
 * - no addition, only base ([preserveBaseEnchants] applies)
 * - addition is an enchanted book (all options apply)
 * - addition is a damageable item and contains enchantments (all options apply)
 * - addition is a non-damageable item and contains enchantments (all options apply)
 *
 */
interface ProcedureEnchanting {

    /**
     * Whether to preserve enchantments from the base.
     *
     * Default: true
     */
    val preserveBaseEnchants: Boolean

    /**
     * The penalty for each enchantment that could not be applied to the result
     *
     * Default: 1
     */
    val conflictPenaltyCost: Int

    /**
     * The penalty for each enchantment that was upgraded
     *
     * Default: 0
     */
    val upgradePenaltyCost: Int

    /**
     * Whether enchantments should be upgraded when levels on base and addition are equal.
     *
     * Default: true
     */
    val upgradeEnchants: Boolean

    fun merge(resultStack: ItemStack, player: ScafallPlayer?, addition: ItemStack) : MergeResult?

    /**
     * The result produced by the [ProcedureEnchanting] procedure.
     */
    interface MergeResult {

        /**
         * The procedure fails if there are only conflicts without at least one enchantment being applied.
         */
        val failed: Boolean

        /**
         * The cost of merging the enchantments and upgrading them.
         */
        val cost: Int

        /**
         * The result with the applied merged enchantments.
         */
        val result: ItemStack

    }

}