package com.wolfyscript.customcrafting.recipes.repair

import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing
import com.wolfyscript.customcrafting.recipes.EvaluationContext
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.process.ProcedureDamageCombine
import com.wolfyscript.customcrafting.recipes.process.ProcedureEnchanting
import com.wolfyscript.customcrafting.recipes.process.ProcedureItemRepair
import com.wolfyscript.customcrafting.recipes.process.ProcedureRename
import com.wolfyscript.customcrafting.recipes.process.ProcessRepairing
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
import net.minecraft.world.item.enchantment.EnchantmentHelper
import kotlin.math.max
import kotlin.random.Random

class CustomProcessRepairingImpl(
    override val rename: ProcedureRename? = null,
    override val damageCombine: ProcedureDamageCombine? = null,
    override val itemRepair: ProcedureItemRepair? = null,
    override val enchanting: ProcedureEnchanting? = null,
) : ProcessRepairing.CustomProcessRepairing {

    override fun compute(
        recipeEvaluationResult: RecipeEvaluationResult<RecipeEvaluationResult.RepairingRecipeData, CustomRecipeRepairing>,
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

        var result = baseStack.copy()
        var cost = 0
        menu.setData(0, 0)

        if (baseStack.isEmpty) {
            menu.setData(0, 0)
            return net.minecraft.world.item.ItemStack.EMPTY.wrap()
        }

        if (additionStack != null) {
            var tryCombineEnchantments = true
            if (itemRepair != null && !additionStack.isDamageableItem && result.isDamageableItem) {
                tryCombineEnchantments = itemRepair.combineEnchants
                // Repair base stack with custom addition item count!
                // While the "repairable" data component defines items/tags that can be used to repair an item, stacks with tags are not possible.
                // This allows for more complex or third-party stacks to be used for repair!
                var repairAmount = result.damageValue.coerceAtMost(result.maxDamage / 4)
                if (repairAmount <= 0) {
                    return net.minecraft.world.item.ItemStack.EMPTY.wrap()
                }

                // ItemStackRefs are allowed to be stacked items, so calculate how many can be used
                val maxRepairCount = additionStack.count / (recipeEvaluationResult.data.bySlot(0)?.matchedItemStackRef?.amount ?: 1)

                for (i in 0 until maxRepairCount) {
                    result.damageValue = result.damageValue - repairAmount
                    repairAmount = result.damageValue.coerceAtMost(result.maxDamage / 4)

                    if (repairAmount <= 0 || i + 1 == maxRepairCount) {
                        cost += (i + 1) * itemRepair.cost
                        recipeEvaluationResult.data.itemRepairCost = i + 1
                        break
                    }
                }
            } else if (damageCombine != null && additionStack.isDamageableItem && result.isDamageableItem) {
                result = damageCombine.combine(baseStack.wrap(), additionStack.wrap(), result.wrap()).unwrap()
            }

            if (enchanting != null && EnchantmentHelper.canStoreEnchantments(result) && tryCombineEnchantments) {
                val mergeResult = enchanting.merge(result.wrap(), player.wrap(), additionStack.wrap())

                if (mergeResult == null || mergeResult.failed) {
                    menu.setData(0, 0)
                    return net.minecraft.world.item.ItemStack.EMPTY.wrap()
                }

                cost += mergeResult.cost
                result = mergeResult.result.unwrap()
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
        return result.wrap()
    }

    override fun toString(): String {
        return "$rename, $damageCombine, $itemRepair, $enchanting)"
    }


}