package com.wolfyscript.customcrafting.spigot.recipes

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toPreciseGlobal
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrap
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import org.bukkit.Material
import org.bukkit.block.Campfire
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockCookEvent
import org.bukkit.event.block.CampfireStartEvent
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.random.Random

class CampfireListener(val customCrafting: CustomCrafting) : Listener {

    /**
     * Applies the correct cook time if there is a custom recipe used.
     */
    @EventHandler
    fun onStart(event: CampfireStartEvent) {
        val source = event.source

        val context = EvaluationContextImpl(null, event.block.location.toPreciseGlobal())
        val input = RecipeInput.CookingRecipeInput.of(source.wrap(), null)
        val data = customCrafting.recipeManager.evaluateRecipesOfType(RecipeTypes.cooking, input, context)

        if (data == null) {
            return
        }

        event.totalCookTime = data.recipe.processing.processingTime
    }

    /**
     * Makes sure that stacked ingredients work.
     * In vanilla ingredients may only have a singular item.
     * In case it is a custom recipe, this uses a custom solution to consume the hand item and place the item.
     *
     * The campfire, at the time of writing, does hold the correctly stacked item and does not alter the amount.
     * So the cook event later on can determine the correct recipe.
     */
    @EventHandler(ignoreCancelled = true)
    fun onPlaceItem(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        val stack = event.item ?: return
        if (stack.type == Material.AIR) {
            return
        }

        val block = event.clickedBlock ?: return
        val state = block.state
        if (state !is Campfire) {
            return
        }
        val slot: Int = (0 until state.size).firstOrNull {
            state.getItem(it)?.type != Material.AIR
        } ?: return // Cannot place item. No empty slot!

        val context = EvaluationContextImpl(null, block.location.toPreciseGlobal())
        val input = RecipeInput.CookingRecipeInput.of(stack.wrap(), null)
        val data = customCrafting.recipeManager.evaluateRecipesOfType(RecipeTypes.cooking, input, context)

        if (data == null) {
            return // No recipe for item. Vanilla behaviour
        }

        val ingredientAmount = data.bySlot(0)?.matchedItemStackRef?.amount ?: 1

        val toPlace = stack.clone().apply {
            amount = ingredientAmount
        }

        stack.amount = stack.amount - ingredientAmount

        state.setItem(slot, toPlace)
        state.setCookTimeTotal(slot, data.recipe.processing.processingTime)
        state.setCookTime(slot, 0)

        event.setUseItemInHand(Event.Result.DENY)
        event.setUseInteractedBlock(Event.Result.DENY)

        state.update()
    }


    /**
     * Cooking is completed and the result is computed.
     */
    @EventHandler
    fun onComplete(event: BlockCookEvent) {
        val block = event.block
        val state = block.state
        if (state !is Campfire) {
            return
        }

        val source = event.source
        val context = EvaluationContextImpl(null, block.location.toPreciseGlobal())
        val input = RecipeInput.CookingRecipeInput.of(source.wrap(), null)
        val data = customCrafting.recipeManager.evaluateRecipesOfType(RecipeTypes.cooking, input, context)
        if (data == null) {
            return
        }
        event.result = data.result.compute(
            data,
            context,
            // No need to store the seed. Cannot determine the result beforehand to cheese it.
            Random(Random.nextLong())
        ).unwrap()
    }

}