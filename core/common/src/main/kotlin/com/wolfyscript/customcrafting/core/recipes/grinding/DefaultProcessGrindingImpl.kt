package com.wolfyscript.customcrafting.core.recipes.grinding

import com.wolfyscript.customcrafting.core.recipes.CustomRecipeGrinding
import com.wolfyscript.customcrafting.core.recipes.EvaluationContext
import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipes.process.ProcedureDamageCombine
import com.wolfyscript.customcrafting.core.recipes.process.ProcedureEnchantRemoval
import com.wolfyscript.customcrafting.core.recipes.process.ProcessGrinding
import com.wolfyscript.customcrafting.core.recipes.process.ProcedureRepairCost
import com.wolfyscript.customcrafting.core.recipes.repair.ProcedureDamageCombineImpl
import com.wolfyscript.customcrafting.core.recipes.process.ProcedureEnchanting
import com.wolfyscript.customcrafting.core.recipes.repair.ProcedureEnchantingImpl
import com.wolfyscript.scafall.wrappers.unwrap
import com.wolfyscript.scafall.wrappers.wrap
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.AnvilMenu
import net.minecraft.world.inventory.GrindstoneMenu
import net.minecraft.world.item.enchantment.EnchantmentHelper
import kotlin.math.ceil
import kotlin.random.Random

class DefaultProcessGrindingImpl(
    override val extraXp: Int = 0,
    override val removeEnchants: ProcedureEnchantRemoval = ProcedureEnchantRemovalImpl(),
    override val mergeEnchants: ProcedureEnchanting = ProcedureEnchantingImpl(true, 0, 0, true),
    override val damageCombine: ProcedureDamageCombine = ProcedureDamageCombineImpl(5, false),
    override val repairCost: ProcedureRepairCost? = null,
) : ProcessGrinding.DefaultProcessGrinding {

    override fun compute(
        recipeEvaluationResult: RecipeEvaluationResult<RecipeEvaluationResult.GrindingRecipeData, CustomRecipeGrinding>,
        input: RecipeInput.GrindingRecipeInput,
        context: EvaluationContext,
        random: Random,
    ): ScafallItemStack {
        val recipe = recipeEvaluationResult.recipe.value ?: return net.minecraft.world.item.ItemStack.EMPTY.wrap()
        val player = context.player?.unwrap() as? ServerPlayer ?: return net.minecraft.world.item.ItemStack.EMPTY.wrap()
        val menu = player.containerMenu
        if (menu !is GrindstoneMenu) {
            return net.minecraft.world.item.ItemStack.EMPTY.wrap()
        }

        var baseStack = input.base?.unwrap()?.copy()
        var additionStack = input.addition?.unwrap()?.copy()
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

        yield += removeEnchants.baseEnchants.removeFrom(result.wrap())
        if (additionStack != null) {
            yield += removeEnchants.additionEnchants.removeFrom(additionStack.wrap())
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
        if (yield > 0) {
            val reduced = ceil(yield / 2.0).toInt()
            yield = reduced + Random.nextInt(reduced)
        } else {
            yield = 0
        }
        recipeEvaluationResult.data.yield = yield + extraXp

        return result.wrap()
    }


}