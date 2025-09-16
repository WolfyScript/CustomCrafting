package com.wolfyscript.customcrafting.recipes.grinding

import com.wolfyscript.customcrafting.recipes.process.ProcedureEnchantRemoval
import com.wolfyscript.customcrafting.recipes.process.SetInclusionExclusionType
import com.wolfyscript.scafall.identifier.toScafall
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.unwrap
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import net.minecraft.tags.EnchantmentTags
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.ItemEnchantments
import kotlin.jvm.optionals.getOrNull

class ProcedureEnchantRemovalImpl(
    override val baseEnchants: IngredientEnchantRemovalProcedureImpl = IngredientEnchantRemovalProcedureImpl(),
    override val additionEnchants: IngredientEnchantRemovalProcedureImpl = IngredientEnchantRemovalProcedureImpl(),
) : ProcedureEnchantRemoval

class IngredientEnchantRemovalProcedureImpl(
    override val removeCurses: Boolean = false,
    override val enchants: List<Key> = emptyList(),
    override val type: SetInclusionExclusionType = SetInclusionExclusionType.KEEP,
) : ProcedureEnchantRemoval.IngredientEnchantRemovalProcedure {

    override fun removeFrom(stack: ScafallItemStack): Int {
        val mcStack = stack.unwrap()
        val itemEnchants = ItemEnchantments.Mutable(mcStack.enchantments)
        var xpYield = 0
        if (type == SetInclusionExclusionType.KEEP) {
            itemEnchants.removeIf { holder ->
                val key = holder.unwrapKey()
                    .map { key -> key.location().toScafall() }.getOrNull()
                if (enchants.contains(key)) {
                    return@removeIf false
                }
                if (!holder.`is`(EnchantmentTags.CURSE) || removeCurses) {
                    xpYield += holder.value().getMinCost(itemEnchants.getLevel(holder))
                    return@removeIf true
                }
                return@removeIf false
            }
        } else {
            itemEnchants.removeIf { holder ->
                val key = holder.unwrapKey()
                    .map { key -> key.location().toScafall() }.getOrNull()
                if (enchants.contains(key) || (holder.`is`(EnchantmentTags.CURSE) && removeCurses)) {
                    xpYield += holder.value().getMinCost(itemEnchants.getLevel(holder))
                    return@removeIf true
                }
                return@removeIf false
            }
        }
        EnchantmentHelper.setEnchantments(mcStack, itemEnchants.toImmutable())
        return xpYield
    }

}