package com.wolfyscript.customcrafting.core.recipes.repair

import com.wolfyscript.customcrafting.core.recipes.process.ProcedureRename

class ProcedureRenameImpl(
    override val formatted: Boolean = false,
    override val prefix: String? = null,
    override val suffix: String? = null,
    override val cost: Int = 1,
    override val increaseRepairCost: Boolean = false
) : ProcedureRename {

    override fun toString(): String {
        return "(formatted: $formatted, prefix: $prefix, suffix: $suffix, cost: $cost, increase: $increaseRepairCost)"
    }
}