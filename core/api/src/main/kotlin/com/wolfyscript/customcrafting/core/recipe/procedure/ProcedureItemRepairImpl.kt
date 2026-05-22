package com.wolfyscript.customcrafting.core.recipe.procedure

internal class ProcedureItemRepairImpl(
    override val cost: Int = 1,
    override val combineEnchants: Boolean = false
) : ProcedureItemRepair