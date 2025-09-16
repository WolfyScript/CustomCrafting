package com.wolfyscript.customcrafting.recipes.repair

import com.wolfyscript.customcrafting.recipes.process.ProcedureDamageCombine
import com.wolfyscript.scafall.wrappers.unwrap
import com.wolfyscript.scafall.wrappers.wrap
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

class ProcedureDamageCombineImpl(
    override val bonusPercentage: Int,
    override val combineDurabilityAsRatio: Boolean
) : ProcedureDamageCombine {

    override fun combine(baseStack: ScafallItemStack, additionStack: ScafallItemStack, resultStack: ScafallItemStack) : ScafallItemStack {
        val base = baseStack.unwrap()
        val addition = additionStack.unwrap()
        val result = resultStack.unwrap()
        // Combine durability
        // Vanilla only allows to combine the durability of the same items.
        // This expands it to allow combining any item
        val baseDur = base.maxDamage - base.damageValue
        val additionDur = addition.maxDamage - addition.damageValue

        val damage = if (combineDurabilityAsRatio) {
            // take the percentages of durability and combine them, so ratios are kept
            val baseDurPerc = baseDur / base.maxDamage
            val additionDurPerc = additionDur / addition.maxDamage
            val totalDurRepairPerc = baseDurPerc + additionDurPerc

            // apply the ratio and bonus based on the max-damage of the result
            val bonusAmount = result.maxDamage * bonusPercentage / 100
            val scalarDur = totalDurRepairPerc * result.maxDamage + bonusAmount

            result.maxDamage - scalarDur
        } else {
            // Simply combine durability
            val bonus = result.maxDamage * bonusPercentage / 100
            val combined = baseDur + additionDur + bonus

            result.maxDamage - combined
        }.coerceAtLeast(0)

        if (damage < result.damageValue) {
            result.damageValue = damage
        }

        return result.wrap()
    }


    override fun toString(): String {
        return "(bonus: $bonusPercentage%, ratio: $combineDurabilityAsRatio)"
    }
}