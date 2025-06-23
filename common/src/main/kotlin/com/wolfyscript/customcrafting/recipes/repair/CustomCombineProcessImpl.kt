package com.wolfyscript.customcrafting.recipes.repair

import com.wolfyscript.customcrafting.recipes.EvaluationContext
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.wrappers.utils.unwrap
import com.wolfyscript.scafall.wrappers.utils.wrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.inventory.AnvilMenu
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.ItemEnchantments
import kotlin.math.max
import kotlin.random.Random

class CustomCombineProcessImpl(
    override val rename: RenameOptions? = null,
    override val damageCombine: DamageCombineOptions? = null,
    override val itemRepair: ItemRepairOptions? = null,
    override val enchanting: EnchantingOptions? = null,
) : CombineProcess.CustomCombineProcess {

    override fun compute(
        recipeData: RecipeData.RepairingRecipeData,
        input: RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
        random: Random,
    ): ItemStack {
        val player = context.player?.unwrap() as? ServerPlayer
        if (player == null) {
            return net.minecraft.world.item.ItemStack.EMPTY.wrap() // Repair recipes require a player!
        }
        val menu = player.containerMenu
        if (menu !is AnvilMenu) {
            return net.minecraft.world.item.ItemStack.EMPTY.wrap() // Must be an Anvil Menu!
        }

        val baseStack = input.base.unwrap()
        val additionStack = input.addition?.unwrap()

        val existingCost = baseStack.getOrDefault(DataComponents.REPAIR_COST, 0) +
                (additionStack?.getOrDefault(DataComponents.REPAIR_COST, 0) ?: 0)

        val result = baseStack.copy()
        var resultEnchants = ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result))
        var cost = 0
        menu.setData(0, 0)

        if (baseStack.isEmpty) {
            menu.setData(0, 0)
            return net.minecraft.world.item.ItemStack.EMPTY.wrap()
        }

        if (additionStack != null) {
            var tryCombineEnchantments = true
            if (itemRepair != null && !additionStack.isDamageableItem && result.isDamageableItem) {
                tryCombineEnchantments = itemRepair.combineEnchantsIfAvailable
                // Repair base stack with custom addition item count!
                // While the "repairable" data component defines items/tags that can be used to repair an item, stacks with tags are not possible.
                // This allows for more complex or third-party stacks to be used for repair!
                var repairAmount = result.damageValue.coerceAtMost(result.maxDamage / 4)
                if (repairAmount <= 0) {
                    return net.minecraft.world.item.ItemStack.EMPTY.wrap()
                }

                // ItemStackRefs are allowed to be stacked items, so calculate how many can be used
                val maxRepairCount = additionStack.count / (recipeData.bySlot(0)?.matchedItemStackRef?.amount ?: 1)

                for (i in 0 until maxRepairCount) {
                    result.damageValue = result.damageValue - repairAmount
                    repairAmount = result.damageValue.coerceAtMost(result.maxDamage / 4)

                    if (repairAmount <= 0) {
                        recipeData.itemRepairCost = i + 1
                        cost += (i + 1) * itemRepair.repairItemCost
                        break
                    }
                }
            } else if (damageCombine != null && additionStack.isDamageableItem && result.isDamageableItem) {
                // Combine durability
                // Vanilla only allows to combine the durability of the same items.
                // This expands it to allow combining any item
                val baseDur = baseStack.maxDamage - baseStack.damageValue
                val additionDur = additionStack.maxDamage - additionStack.damageValue

                val damage = if (damageCombine.combineDurabilityAsRatio) {
                    // take the percentages of durability and combine them, so ratios are kept
                    val baseDurPerc = baseDur / baseStack.maxDamage
                    val additionDurPerc = additionDur / additionStack.maxDamage
                    val totalDurRepairPerc = baseDurPerc + additionDurPerc

                    // apply the ratio and bonus based on the max-damage of the result
                    val bonusAmount = result.maxDamage * damageCombine.bonusPercentage / 100
                    val scalarDur = totalDurRepairPerc * result.maxDamage + bonusAmount

                    result.maxDamage - scalarDur
                } else {
                    // Simply combine durability
                    val bonus = result.maxDamage * damageCombine.bonusPercentage / 100
                    val combined = baseDur + additionDur + bonus

                    result.maxDamage - combined
                }.coerceAtLeast(0)

                if (damage < result.damageValue) {
                    result.damageValue = damage
                }
            }

            if (enchanting != null && EnchantmentHelper.canStoreEnchantments(result) && tryCombineEnchantments) {
                // combine enchantments if possible/necessary
                val additionEnchants = EnchantmentHelper.getEnchantmentsForCrafting(additionStack)

                var appliesAtLeastOneEnchant = false
                var hasIncompatibleEnchants = false

                if (!enchanting.preserveBaseEnchants) {
                    resultEnchants = ItemEnchantments.Mutable(ItemEnchantments.EMPTY)
                }

                for (entry in additionEnchants.entrySet()) {
                    val enchantmentHolder = entry.key
                    val enchantment = enchantmentHolder.value()

                    val compatible =
                        if (player.hasInfiniteMaterials() || result.`is`(Items.ENCHANTED_BOOK)) {
                            true
                        } else {
                            enchantment.canEnchant(result)
                        }
                    val conflicts = resultEnchants.keySet().count {
                        !it.equals(enchantmentHolder) && !Enchantment.areCompatible(it, enchantmentHolder)
                    }

                    if (!compatible || conflicts > 0) {
                        cost += conflicts * enchanting.conflictPenaltyCost
                        hasIncompatibleEnchants = true
                        continue
                    }

                    appliesAtLeastOneEnchant = true
                    val resultLvl = resultEnchants.getLevel(enchantmentHolder)
                    val additionLvl = entry.intValue
                    val upgradedLvl = if (enchanting.upgradeEnchants) {
                        if (resultLvl == additionLvl) {
                            additionLvl + 1
                        } else {
                            max(resultLvl, additionLvl)
                        }.coerceAtMost(enchantment.maxLevel)
                    } else {
                        if (resultLvl == 0) additionLvl else resultLvl
                    }

                    resultEnchants.set(enchantmentHolder, upgradedLvl)

                    // calculate enchantment cost
                    var enchantCost = enchantment.anvilCost
                    if (result.has(DataComponents.STORED_ENCHANTMENTS)) {
                        enchantCost = max(1, enchantCost / 2)
                    }
                    cost += enchantCost * upgradedLvl
                }

                if (hasIncompatibleEnchants && !appliesAtLeastOneEnchant) {
                    menu.setData(0, 0)
                    return net.minecraft.world.item.ItemStack.EMPTY.wrap()
                }
            }
        }

        var renameCost = 0
        if (rename != null) {
            // rename item
            renameCost = if (!input.itemName.isNullOrBlank()) {
                if (input.itemName!! != baseStack.hoverName.string) {
                    val name = if (rename.formatted) {
                        input.itemName!!.deser().vanilla()
                    } else {
                        Component.literal(input.itemName!!)
                    }
                    result.set(DataComponents.CUSTOM_NAME, name)

                    rename.cost
                } else 0
            } else if (baseStack.has(DataComponents.CUSTOM_NAME)) {
                result.remove(DataComponents.CUSTOM_NAME)

                rename.cost
            } else 0
            cost += renameCost
        }

        // apply cost to menu
        val totalCost = if (cost <= 0) 0 else Mth.clamp(cost + existingCost, 0, Int.MAX_VALUE)
        menu.setData(0, totalCost)

        // apply new repair cost to result item
        var increasedRepairCost = max(
            baseStack.getOrDefault(DataComponents.REPAIR_COST, 0),
            additionStack?.getOrDefault(DataComponents.REPAIR_COST, 0) ?: 0
        )

        if (renameCost != cost || renameCost == 0 || rename?.increaseRepairCost == true) {
            // only increase cost when repairing and/or enchanting, or specifically enabled
            increasedRepairCost = AnvilMenu.calculateIncreasedRepairCost(increasedRepairCost)
        }

        result.set(DataComponents.REPAIR_COST, increasedRepairCost)
        EnchantmentHelper.setEnchantments(result, resultEnchants.toImmutable())

        return result.wrap()
    }

    override fun toString(): String {
        return "$rename, $damageCombine, $itemRepair, $enchanting)"
    }


}