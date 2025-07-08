package com.wolfyscript.customcrafting.recipes.grinding

import com.wolfyscript.customcrafting.recipes.CustomRecipeGrinding
import com.wolfyscript.customcrafting.recipes.EvaluationContext
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.repair.DamageCombineOptionsImpl
import com.wolfyscript.customcrafting.recipes.repair.EnchantingOptions
import com.wolfyscript.customcrafting.recipes.repair.EnchantingOptionsImpl
import com.wolfyscript.scafall.wrappers.utils.unwrap
import com.wolfyscript.scafall.wrappers.utils.wrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.AnvilMenu
import net.minecraft.world.inventory.GrindstoneMenu
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.ItemEnchantments
import kotlin.random.Random

class DefaultGrindingProcessImpl(
    override val extraXp: Int = 0,
    override val removeEnchants: EnchantRemovalOptionsImpl = EnchantRemovalOptionsImpl(),
    override val mergeEnchants: EnchantingOptions = EnchantingOptionsImpl(true, 0, 0, true),
    override val damageCombine: DamageCombineOptionsImpl = DamageCombineOptionsImpl(5, false),
    override val repairCost: RepairCostOptions? = null,
) : GrindingProcess.DefaultGrindingProcess {

    override fun compute(
        recipeEvaluationResult: RecipeEvaluationResult<RecipeEvaluationResult.GrindingRecipeData, CustomRecipeGrinding>,
        input: RecipeInput.GrindingRecipeInput,
        context: EvaluationContext,
        random: Random,
    ): ItemStack {
        val recipe = recipeEvaluationResult.recipe.value
        if (recipe == null) {
            return net.minecraft.world.item.ItemStack.EMPTY.wrap()
        }
        val player = context.player?.unwrap() as? ServerPlayer
        if (player == null) {
            return net.minecraft.world.item.ItemStack.EMPTY.wrap()
        }
        val menu = player.containerMenu
        if (menu !is GrindstoneMenu) {
            return net.minecraft.world.item.ItemStack.EMPTY.wrap()
        }

        var baseStack = input.base?.unwrap()
        var additionStack = input.addition?.unwrap()
        if (baseStack == null && additionStack == null) {
            return net.minecraft.world.item.ItemStack.EMPTY.wrap()
        }
        if (recipe.addition == null) {
            if (baseStack == null) {
                baseStack = additionStack
                additionStack = null
            }
        }

        if (baseStack == null) { // required base stack should not be null at this point
            return net.minecraft.world.item.ItemStack.EMPTY.wrap()
        }

        var result = baseStack.copy()
        var penalty = 0
        var yield = 0

        val baseEnchants = ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(baseStack))
        yield += removeEnchants.baseEnchants.removeFrom(baseEnchants)
        if (additionStack != null) {
            val additionEnchants = ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(additionStack))
            yield += removeEnchants.additionEnchants.removeFrom(additionEnchants)
        }

        if (EnchantmentHelper.canStoreEnchantments(result) && additionStack != null) {
            val mergeResult = mergeEnchants.merge(result.wrap(), player.wrap(), additionStack.wrap())

            if (mergeResult == null || mergeResult.failed) {
                return net.minecraft.world.item.ItemStack.EMPTY.wrap()
            }
            result = mergeResult.result.unwrap()
            penalty += mergeResult.cost
        }

        if (additionStack != null && !additionStack.isDamageableItem && result.isDamageableItem) {
            result = damageCombine.combine(baseStack.wrap(), additionStack.wrap(), result.wrap()).unwrap()
        }

        if (repairCost != null) {
            var cost = 0
            for (i in 0 until result.enchantments.size()) {
                cost = AnvilMenu.calculateIncreasedRepairCost(cost)
            }

            result.set(DataComponents.REPAIR_COST, cost + repairCost.increasedCost)
        }

        recipeEvaluationResult.data.penalty = penalty
        recipeEvaluationResult.data.yield = yield + extraXp
        return result.wrap()
    }


}