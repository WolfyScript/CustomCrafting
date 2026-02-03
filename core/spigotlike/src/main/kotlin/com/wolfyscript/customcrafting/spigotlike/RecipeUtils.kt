package com.wolfyscript.customcrafting.spigotlike

import com.wolfyscript.customcrafting.core.recipes.EvaluationContext
import com.wolfyscript.customcrafting.core.recipes.RecipeResult
import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrapSpigot
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import kotlin.random.Random

fun possibleResultAmount(recipeEvaluationResult: RecipeEvaluationResult<*, *>, sourceStacks: List<com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack>): Int =
    recipeEvaluationResult.data.nonNullIngredients.withIndex().minOf { (index, value) ->
        sourceStacks[index].amount / value.matchedItemStackRef.amount
    }

fun collectResultAndRunActions(
    event: InventoryClickEvent,
    targetInv: Inventory,
    craftingData: RecipeEvaluationResult<*, *>,
    recipeResult: RecipeResult,
    sourceStacks: List<com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack>,
    context: EvaluationContext,
    random: Random,
): Int {
    var maxPossible = possibleResultAmount(craftingData, sourceStacks)

    if (!event.isShiftClick) {
        if (maxPossible <= 0) {
            return 0
        }
        val result = recipeResult.compute(craftingData, context, random).unwrapSpigot()

        val cursor = event.cursor
        if (cursor.type == Material.AIR || (result.isSimilar(cursor) && cursor.amount + result.amount <= cursor.maxStackSize)) {
            if (cursor.type == Material.AIR) {
                event.view.setCursor(result)
            } else {
                cursor.amount += result.amount
            }
            recipeResult.runActions(context, 1)
            return 1
        }
        return 0
    }

    if (event.isShiftClick) {
        maxPossible = quickCraft(maxPossible, targetInv, craftingData, recipeResult, context, random)
        recipeResult.runActions(context, maxPossible)
        return maxPossible
    }
    return 0
}

fun quickCraft(
    maxPossible: Int,
    targetInv: Inventory,
    craftingData: RecipeEvaluationResult<*, *>,
    recipeResult: RecipeResult,
    context: EvaluationContext,
    random: Random,
): Int {
    for (i in 0..<maxPossible) {
        val stack = recipeResult.compute(craftingData, context, random).unwrapSpigot()
        val stackCopy = stack.clone()
        val remains = targetInv.addItem(stackCopy)
        if (remains.isNotEmpty()) {
            // revert the last added stack again, by removing what was not added
            val toRemove = stack.amount - remains[0]!!.amount
            stack.amount = toRemove
            targetInv.removeItem(stack)
            return i
        }
    }
    return maxPossible
}
