package com.wolfyscript.customcrafting.paper.recipes

import com.github.benmanes.caffeine.cache.Caffeine
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.recipes.CustomRecipeStonecutting
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeReferenceImpl
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.getRecipeTyped
import com.wolfyscript.customcrafting.spigotlike.collectResultAndRunActions
import com.wolfyscript.customcrafting.spigotlike.recipes.isPlaceholder
import com.wolfyscript.customcrafting.spigotlike.recipes.originalRecipeKey
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toPreciseGlobal
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrapSpigot
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.StonecutterInventory
import java.util.*
import kotlin.random.Random

private const val INPUT_SLOT = 0
private const val RESULT_SLOT = 1

class StonecutterListener(val customCrafting: CustomCrafting) : Listener {

    private val recipeCache = Caffeine.newBuilder()
        .build<UUID, RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeStonecutting>>()

    @EventHandler
    private fun onRecipeSelect(event: PlayerStonecutterRecipeSelectEvent) {
        val bukkitRecipe = event.stonecuttingRecipe
        recipeCache.invalidate(event.player.uniqueId)
        if (!bukkitRecipe.isPlaceholder()) {
            return
        }
        val key = bukkitRecipe.originalRecipeKey()
        val recipe = customCrafting.recipeManager.getRecipeTyped(key, RecipeTypes.stonecutting.resolveOrThrow())?.value ?: return
        event.isCancelled = true
        val source = event.stonecutterInventory.getItem(INPUT_SLOT) ?: ItemStack(Material.AIR)
        val context = EvaluationContextImpl(event.player.wrap(), event.player.location.toPreciseGlobal())
        val data = recipe.evaluate(RecipeInput.SingleSlotRecipeInput.of(source.wrap()), context)

        if (data != null) {
            val evalResult = RecipeEvaluationResultImpl(RecipeReferenceImpl(key, recipe), data)
            event.stonecutterInventory.result = recipe.result.compute(evalResult, context, Random).unwrapSpigot()
            recipeCache.put(event.player.uniqueId, evalResult)
        } else {
            event.stonecutterInventory.result = ItemStack(Material.AIR)
        }
    }

    @EventHandler
    private fun onResultCollect(event: InventoryClickEvent) {
        val inventory = event.inventory as? StonecutterInventory ?: return
        val player = event.whoClicked as? Player ?: return
        if (event.slot != RESULT_SLOT) {
            return
        }
        val result = inventory.result
        if (result == null || result.isEmpty) {
            event.isCancelled = true
            return
        }
        if (!event.isShiftClick && (!result.isSimilar(event.cursor) || result.amount + event.cursor.amount > event.cursor.maxStackSize)) {
            event.isCancelled = true
            return
        }
        val evalResult = recipeCache.getIfPresent(player.uniqueId) ?: return
        val recipe = evalResult.recipe.value ?: return
        val context = EvaluationContextImpl(player.wrap(), player.location.toPreciseGlobal())

        val maxPossible = collectResultAndRunActions(
            event,
            player.inventory,
            evalResult,
            recipe.result,
            listOf(result.wrap()),
            context,
            Random
        )

        evalResult.data.bySlot(INPUT_SLOT)?.let { source ->
            inventory.getItem(INPUT_SLOT)?.let {
                source.selectedIngredient.shrink(
                    it.wrap(),
                    maxPossible,
                    source.matchedItemStackRef,
                    context,
                    evalResult
                )
            }
        }

    }

}