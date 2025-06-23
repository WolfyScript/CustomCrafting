package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.data.RepairingRecipeDataImpl
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.wrappers.utils.unwrap
import com.wolfyscript.scafall.wrappers.utils.wrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth.clamp
import net.minecraft.world.inventory.AnvilMenu
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.ItemEnchantments
import kotlin.math.max
import kotlin.random.Random

class CustomRecipeRepairingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val process: CustomRecipeRepairing.RepairProcess,
    override val base: Ingredient,
    override val addition: Ingredient?,
) : CustomRecipeRepairing {

    override fun evaluate(
        input: RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
    ): RecipeData.RepairingRecipeData? {
        val matchedBase = base.match(input.base)?.let { baseMatch ->
            IngredientDataImpl(1, 1, base, baseMatch)
        } ?: return null

        if (addition == null && input.addition != null || addition != null && input.addition == null) {
            return null
        }
        val matchedAddition = addition?.match(input.addition!!)?.let { additionMatch ->
            IngredientDataImpl(2, 2, addition, additionMatch)
        } ?: return null

        return RepairingRecipeDataImpl(0, this, arrayOf(matchedBase, matchedAddition))
    }

    class FixedResultImpl(override val result: RecipeResult, override val cost: Int?) :
        CustomRecipeRepairing.RepairProcess.FixedResult {

        override fun compute(
            recipeData: RecipeData.RepairingRecipeData,
            input: RecipeInput.RepairingRecipeInput,
            context: EvaluationContext,
            random: Random,
        ): ItemStack {
            return result.compute(recipeData, context, random)
        }

    }

    class CustomDamageImpl(
        override val additionalRepair: Int?,
        override val combineDurabilityAsRatio: Boolean = false,
    ) : CustomRecipeRepairing.RepairProcess.CustomDamageRepair {

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
            val resultEnchants = ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result))
            var cost = 0

            if (additionStack != null) {
                if (additionStack.isDamageableItem) {
                    // Combine durability
                    // Vanilla only allows to combine the durability of the same items.
                    // This expands it to allow combining any item
                    if (baseStack.isDamageableItem) {
                        val baseDur = baseStack.maxDamage - baseStack.damageValue
                        val additionDur = additionStack.maxDamage - additionStack.damageValue

                        val damage = if (combineDurabilityAsRatio) {
                            // take the percentages of durability and combine them, so ratios are kept
                            val baseDurPerc = baseDur / baseStack.maxDamage
                            val additionDurPerc = additionDur / additionStack.maxDamage
                            val totalDurRepairPerc = baseDurPerc + additionDurPerc

                            // apply the ratio and bonus based on the max-damage of the result
                            val bonusAmount = result.maxDamage * 12 / 100
                            val scalarDur = totalDurRepairPerc * result.maxDamage + bonusAmount

                            result.maxDamage - scalarDur
                        } else {
                            // Simply combine durability
                            val bonus = result.maxDamage * 12 / 100
                            val combined = baseDur + additionDur + bonus

                            result.maxDamage - combined
                        }.coerceAtLeast(0)

                        if (damage < result.damageValue) {
                            result.damageValue = damage
                        }

                        // combine enchantments if possible
                        val additionItems = EnchantmentHelper.getEnchantmentsForCrafting(additionStack)

                        var appliesAtLeastOneEnchant = false
                        var hasIncompatibleEnchants = false

                        for (entry in additionItems.entrySet()) {
                            val enchantmentHolder = entry.key
                            val enchantment = enchantmentHolder.value()

                            val compatible = if (player.hasInfiniteMaterials() || result.`is`(Items.ENCHANTED_BOOK)) {
                                true
                            } else {
                                enchantment.canEnchant(result)
                            }
                            val conflicts = resultEnchants.keySet().count {
                                !it.equals(enchantmentHolder) && !Enchantment.areCompatible(it, enchantmentHolder)
                            }

                            if (!compatible || conflicts > 0) {
                                cost += conflicts
                                hasIncompatibleEnchants = true
                                continue
                            }

                            appliesAtLeastOneEnchant = true
                            val resultLvl = resultEnchants.getLevel(enchantmentHolder)
                            val additionLvl = entry.intValue
                            val upgradedLvl = if (resultLvl == additionLvl) {
                                additionLvl + 1
                            } else {
                                max(resultLvl, additionLvl)
                            }.coerceAtMost(enchantment.maxLevel)

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
                } else {
                    // Repair stack with custom addition item
                    // While the "repairable" data component defines items/tags that can be used to repair an item, stacks with tags are not possible.
                    // This allows for more complex or third-party stacks to be used for repair!
                    var repairAmount = result.damageValue.coerceAtLeast(result.maxDamage)
                    if (repairAmount <= 0) {
                        return net.minecraft.world.item.ItemStack.EMPTY.wrap()
                    }

                    // ItemStackRefs are allowed to be stacked items, so calculate how many can be used
                    val maxRepairCount = additionStack.count / (recipeData.bySlot(0)?.matchedItemStackRef?.amount ?: 1)

                    for (i in 0 until maxRepairCount) {
                        result.damageValue = result.damageValue - repairAmount
                        repairAmount = result.damageValue.coerceAtMost(result.maxDamage)

                        if (repairAmount <= 0) {
                            recipeData.itemRepairCost = i + 1
                            break
                        }
                    }
                }

            }

            // rename item
            val renameCost = if (!input.itemName.isNullOrBlank()) {
                if (input.itemName!! != baseStack.hoverName.string) {
                    result.set(
                        DataComponents.CUSTOM_NAME,
                        input.itemName!!.deser().vanilla()
                    ) // Use minimessage for styled name TODO: make optional
                    1
                } else 0
            } else if (baseStack.has(DataComponents.CUSTOM_NAME)) {
                result.remove(DataComponents.CUSTOM_NAME)
                1
            } else 0
            cost += renameCost

            // apply cost to menu
            val totalCost = if (cost <= 0) 0 else clamp(cost + existingCost, 0, Int.MAX_VALUE)
            menu.setData(0, totalCost)

            // apply new repair cost to result item
            var increasedRepairCost = max(
                baseStack.getOrDefault(DataComponents.REPAIR_COST, 0),
                additionStack?.getOrDefault(DataComponents.REPAIR_COST, 0) ?: 0
            )

            if (renameCost != cost || renameCost == 0) { // only increase cost when repairing and/or enchanting
                increasedRepairCost = AnvilMenu.calculateIncreasedRepairCost(increasedRepairCost)
            }

            result.set(DataComponents.REPAIR_COST, increasedRepairCost)
            EnchantmentHelper.setEnchantments(result, resultEnchants.toImmutable())

            return result.wrap()
        }

    }

}

