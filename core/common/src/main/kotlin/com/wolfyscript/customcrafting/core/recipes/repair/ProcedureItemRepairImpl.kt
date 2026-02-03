package com.wolfyscript.customcrafting.core.recipes.repair

import com.wolfyscript.customcrafting.core.recipes.process.ProcedureItemRepair

class ProcedureItemRepairImpl(
    override val cost: Int = 1,
    override val combineEnchants: Boolean = false
) : ProcedureItemRepair {
}