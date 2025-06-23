package com.wolfyscript.customcrafting.recipes.repair

class EnchantingOptionsImpl(
    override val preserveBaseEnchants: Boolean = true,
    override val conflictPenaltyCost: Int = 1,
    override val upgradeEnchants: Boolean = true,
) : EnchantingOptions {

    override fun toString(): String {
        return "(preserveBase: $preserveBaseEnchants, penalty: $conflictPenaltyCost, upgrade: $upgradeEnchants)"
    }
}