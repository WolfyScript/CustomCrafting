package com.wolfyscript.customcrafting.recipes.repair

class DamageCombineOptionsImpl(
    override val bonusPercentage: Int,
    override val combineDurabilityAsRatio: Boolean
) : DamageCombineOptions {

    override fun toString(): String {
        return "(bonus: $bonusPercentage%, ratio: $combineDurabilityAsRatio)"
    }
}