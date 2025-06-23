package com.wolfyscript.customcrafting.recipes.repair

/**
 * Settings for the scenario that the base is repaired with a non-damageable addition that isn't an enchanted book.
 *
 *
 */
interface ItemRepairOptions {

    /**
     * The cost when using repair items to repair an item. (e.g. diamond to repair a diamond sword)
     *
     * Must be >= 1; Default: 1
     */
    val repairItemCost: Int

    /**
     * Whether to combine the enchantments from the repair item (if any), with the base.
     *
     * Default: false
     */
    val combineEnchantsIfAvailable: Boolean
}