package com.wolfyscript.customcrafting.recipes.grinding

interface EnchantMergeOptions {

    /**
     * Whether enchantments should be upgraded when they match in level.
     */
    val upgradeEnchants: Boolean

    /**
     * Optional penalty to remove from the calculated experience for each enchantment conflict.
     * Negative values are also allowed and would add xp for each conflict.
     */
    val conflictXpPenalty: Int

    /**
     * Optional penalty to remove from the calculated experience for each enchantment upgrade.
     * Negative values are also allowed and would add xp for each upgrade.
     */
    val upgradeXpPenalty: Int

}