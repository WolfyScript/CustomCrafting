package com.wolfyscript.customcrafting.recipes.repair

import com.wolfyscript.customcrafting.recipes.process.ProcedureEnchanting
import com.wolfyscript.scafall.wrappers.ScafallPlayer
import com.wolfyscript.scafall.wrappers.utils.unwrap
import com.wolfyscript.scafall.wrappers.utils.wrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.ItemEnchantments
import kotlin.math.max

class ProcedureEnchantingImpl(
    override val preserveBaseEnchants: Boolean = true,
    override val conflictPenaltyCost: Int = 1,
    override val upgradePenaltyCost: Int = 0,
    override val upgradeEnchants: Boolean = true,
) : ProcedureEnchanting {

    override fun merge(resultStack: ItemStack, player: ScafallPlayer?, addition: ItemStack) : MergeResultImpl? {
        val additionEnchants = addition.unwrap().enchantments
        val result = resultStack.unwrap()
        val resultEnchants = if (!preserveBaseEnchants) {
            ItemEnchantments.Mutable(ItemEnchantments.EMPTY)
        } else {
            ItemEnchantments.Mutable(result.enchantments)
        }

        var appliesAnEnchant = false
        var hasIncompatibleEnchants = false

        var cost = 0

        for (entry in additionEnchants.entrySet()) {
            val enchantmentHolder = entry.key
            val enchantment = enchantmentHolder.value()

            val compatible =
                (player?.unwrap()?.hasInfiniteMaterials() == true || result.`is`(Items.ENCHANTED_BOOK)) || enchantment.canEnchant(
                    result
                )
            val conflicts = resultEnchants.keySet().count {
                !it.equals(enchantmentHolder) && !Enchantment.areCompatible(it, enchantmentHolder)
            }

            if (!compatible || conflicts > 0) {
                cost += conflicts * conflictPenaltyCost
                hasIncompatibleEnchants = true
                continue
            }

            appliesAnEnchant = true
            val resultLvl = resultEnchants.getLevel(enchantmentHolder)
            val additionLvl = entry.intValue

            if (upgradeEnchants) {
                val upgradedLvl = if (resultLvl == additionLvl) {
                    additionLvl + 1
                } else {
                    max(1, max(resultLvl, additionLvl))
                }.coerceAtMost(enchantment.maxLevel)

                if (upgradedLvl > resultLvl) {
                    cost += upgradePenaltyCost
                }

                resultEnchants.set(enchantmentHolder, upgradedLvl)

                // calculate enchantment cost
                var enchantCost = enchantment.anvilCost
                if (result.has(DataComponents.STORED_ENCHANTMENTS)) {
                    enchantCost = max(1, enchantCost / 2)
                }
                cost += enchantCost * upgradedLvl
            } else {
                resultEnchants.set(enchantmentHolder, resultLvl)
            }

        }

        EnchantmentHelper.setEnchantments(result, resultEnchants.toImmutable())

        return MergeResultImpl(hasIncompatibleEnchants && !appliesAnEnchant, cost, result.wrap())
    }

    override fun toString(): String {
        return "(preserveBase: $preserveBaseEnchants, penalty: $conflictPenaltyCost, upgrade: $upgradeEnchants)"
    }

    class MergeResultImpl(
        override val failed: Boolean,
        override val cost: Int,
        override val result: ItemStack
    ) : ProcedureEnchanting.MergeResult {

    }
}