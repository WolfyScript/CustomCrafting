package com.wolfyscript.customcrafting.recipes.repair

class ItemRepairOptionsImpl(
    override val repairItemCost: Int = 1,
    override val combineEnchantsIfAvailable: Boolean = false
) : ItemRepairOptions {
}