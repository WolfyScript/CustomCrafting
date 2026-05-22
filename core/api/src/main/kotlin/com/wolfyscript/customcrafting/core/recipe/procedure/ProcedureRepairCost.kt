package com.wolfyscript.customcrafting.core.recipe.procedure

/**
 * Specifies how the repair cost is applied to a result item.
 */
interface ProcedureRepairCost {

    /**
     * The amount of repair cost that gets added to the existing repair cost of the ingredients and put on the result.
     */
    val increasedCost: Int

}