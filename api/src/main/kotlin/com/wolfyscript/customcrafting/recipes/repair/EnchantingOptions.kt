package com.wolfyscript.customcrafting.recipes.repair

/**
 * Specifies how enchantments are combined or preserved.
 * This applies to almost all scenarios that involve enchantments in a way, like:
 * - no addition, only base ([preserveBaseEnchants] applies)
 * - addition is an enchanted book (all options apply)
 * - addition is a damageable item and contains enchantments (all options apply)
 * - addition is a non-damageable item and contains enchantments and [ItemRepairOptions.combineEnchants] is enabled (all options apply)
 *
 */
interface EnchantingOptions {

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
     * Whether enchantments should be upgraded when levels on base and addition are equal.
     *
     * Default: true
     */
    val upgradeEnchants: Boolean

}