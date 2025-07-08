package com.wolfyscript.customcrafting.recipes.repair

import com.wolfyscript.scafall.wrappers.world.items.ItemStack

/**
 * Settings for the scenario that both base and addition in an Anvil are damageable items.
 * Their durability (max damage - damage) is then combined.
 */
interface DamageCombineOptions {

    /**
     * The percentage of the maximum damage (durability) applied to the result when repairing with another damageable item.
     *
     * Default: 12% of maximum damage
     */
    val bonusPercentage: Int

    /**
     * Whether the durability of the base item and additional item (if it is damageable) should be combined
     * in percentages instead of combining the actual durability values (like in vanilla).
     *
     * This means it is independent of the max damage difference between the items. An addition with vastly
     * more max damage won't benefit the repair process.
     */
    val combineDurabilityAsRatio: Boolean

    fun combine(baseStack: ItemStack, additionStack: ItemStack, resultStack: ItemStack) : ItemStack

}