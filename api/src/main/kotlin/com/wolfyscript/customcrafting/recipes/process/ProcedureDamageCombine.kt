package com.wolfyscript.customcrafting.recipes.process

import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

/**
 * Controls how the durability of two items is combined and applied to the resulting stack.
 *
 * If the addition is not a damageable item see [ProcedureItemRepair]
 */
interface ProcedureDamageCombine {

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

    /**
     * Combines the durability (max damage - damage) of the base and addition items and applies it to the result.
     * Both base and addition need to be damageable items, so their durability can be combined.
     */
    fun combine(baseStack: ScafallItemStack, additionStack: ScafallItemStack, resultStack: ScafallItemStack) : ScafallItemStack

}