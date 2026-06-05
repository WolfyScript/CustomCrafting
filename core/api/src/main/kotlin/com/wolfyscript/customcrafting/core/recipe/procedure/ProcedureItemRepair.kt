package com.wolfyscript.customcrafting.core.recipe.procedure

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * Specifies how a base damageable item is repaired using non-damageable stackable addition items.
 */
@JsonDeserialize(`as` = ProcedureItemRepairImpl::class)
interface ProcedureItemRepair {

    /**
     * The cost when using repair items to repair an item. (e.g. diamond to repair a diamond sword)
     *
     * Must be >= 1; Default: 1
     */
    val cost: Int

    /**
     * Whether to combine the enchantments from the repair item (if any), with the base.
     *
     * Default: false
     */
    val combineEnchants: Boolean
}