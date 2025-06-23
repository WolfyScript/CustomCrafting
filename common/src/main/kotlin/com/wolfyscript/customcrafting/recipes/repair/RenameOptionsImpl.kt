package com.wolfyscript.customcrafting.recipes.repair

class RenameOptionsImpl(
    override val formatted: Boolean = false,
    override val prefix: String? = null,
    override val suffix: String? = null,
    override val cost: Int = 1,
    override val increaseRepairCost: Boolean = false
) : RenameOptions {

    override fun toString(): String {
        return "(formatted: $formatted, prefix: $prefix, suffix: $suffix, cost: $cost, increase: $increaseRepairCost)"
    }
}