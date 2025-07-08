package com.wolfyscript.customcrafting.recipes.grinding

import com.wolfyscript.scafall.identifier.Key
import net.minecraft.tags.EnchantmentTags
import net.minecraft.world.item.enchantment.ItemEnchantments
import kotlin.jvm.optionals.getOrNull

class EnchantRemovalOptionsImpl(
    override val baseEnchants: IngredientEnchantRemovalOptionsImpl = IngredientEnchantRemovalOptionsImpl(),
    override val additionEnchants: IngredientEnchantRemovalOptionsImpl = IngredientEnchantRemovalOptionsImpl(),
) : EnchantRemovalOptions

class IngredientEnchantRemovalOptionsImpl(
    override val removeCurses: Boolean = false,
    override val enchants: List<Key> = emptyList(),
    override val type: SetInclusionExclusionType = SetInclusionExclusionType.KEEP,
) : EnchantRemovalOptions.IngredientEnchantRemovalOptions {

    fun removeFrom(itemEnchants: ItemEnchantments.Mutable) : Int {
        var xpYield = 0
        if (type == SetInclusionExclusionType.KEEP) {
            itemEnchants.removeIf { holder ->
                val key = holder.unwrapKey()
                    .map { key -> key.location().let { Key.key(it.namespace, it.path) } }.getOrNull()
                if (enchants.contains(key)) {
                    return@removeIf false
                }
                if (!holder.`is`(EnchantmentTags.CURSE) || removeCurses) {
                    xpYield += holder.value().minLevel
                    return@removeIf true
                }
                return@removeIf false
            }
        } else {
            itemEnchants.removeIf { holder ->
                val key = holder.unwrapKey()
                    .map { key -> key.location().let { Key.key(it.namespace, it.path) } }.getOrNull()
                if (enchants.contains(key) || (holder.`is`(EnchantmentTags.CURSE) && removeCurses)) {
                    xpYield += holder.value().minLevel
                    return@removeIf true
                }
                return@removeIf false
            }
        }

        return xpYield
    }

}