package com.wolfyscript.customcrafting.recipes.repair

import com.wolfyscript.customcrafting.recipes.process.ProcedureItemRepair

class ProcedureItemRepairImpl(
    override val cost: Int = 1,
    override val combineEnchants: Boolean = false
) : ProcedureItemRepair {
}