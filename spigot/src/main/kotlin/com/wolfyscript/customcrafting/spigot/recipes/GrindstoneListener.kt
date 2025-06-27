package com.wolfyscript.customcrafting.spigot.recipes

import com.github.benmanes.caffeine.cache.Caffeine
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.recipes.CustomRecipeGrinding
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.spigot.CustomCraftingSpigot
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toPreciseGlobal
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrap
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.random.Random

class GrindstoneListener(val customCrafting: CustomCrafting) : Listener {

    private val recipeCache = Caffeine.newBuilder().build<UUID, RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeGrinding>>()

    @EventHandler
    fun onCollectResult(event: InventoryClickEvent) {
        val inventory = event.clickedInventory
        if (inventory == null || event.action == InventoryAction.NOTHING || (inventory.type != InventoryType.GRINDSTONE)) {
            return
        }
        if (event.slotType != InventoryType.SlotType.RESULT) {
            return
        }
        val player = event.whoClicked as Player
        val action = event.action

        val data = recipeCache.getIfPresent(player.uniqueId) ?: return
        val recipe = data.recipe.value ?: return

        event.isCancelled = true // Block vanilla behaviour of just removing the entire input

        val result = event.currentItem
        val cursor = event.cursor

        if (event.isShiftClick) {
            // TODO: manual result collection
        }

        val context =
            EvaluationContextImpl((event.view.player as Player).wrap(), event.inventory.location?.toPreciseGlobal())

        if (recipe.xp > 0) {
            val orb: ExperienceOrb = player.location.world.spawnEntity(player.location, EntityType.EXPERIENCE_ORB) as ExperienceOrb
            orb.experience = recipe.xp
        }

        recipe.result.runActions(context)

        // TODO: Craft remains
        data.data.bySlot(0)?.let {
            inventory.getItem(0)?.apply {
                amount = amount - it.matchedItemStackRef.amount
            }
        }

        data.data.bySlot(1)?.let {
            inventory.getItem(1)?.apply {
                amount = amount - it.matchedItemStackRef.amount
            }
        }

        recipeCache.invalidate(player.uniqueId)
        player.persistentDataContainer.set(CustomCraftingSpigot.playerGrindingSeedKey, PersistentDataType.LONG, Random.Default.nextLong())
    }

    @EventHandler
    fun onPrepare(event: PrepareGrindstoneEvent) {
        recipeCache.invalidate(event.view.player.uniqueId)

        val context =
            EvaluationContextImpl((event.view.player as Player).wrap(), event.inventory.location?.toPreciseGlobal())
        val input =
            RecipeInput.GrindingRecipeInput.of(event.inventory.getItem(0)?.wrap(), event.inventory.getItem(1)?.wrap())

        val data = customCrafting.recipeManager.evaluateRecipesOfType(RecipeTypes.grinding.resolveOrThrow(), input, context) ?: return // Not a custom recipe
        val recipe = data.recipe.value ?: return

        event.result = recipe.result.compute(data, context, Random(getGrindingSeed(event.view.player as Player))).unwrap()

        recipeCache.put(event.view.player.uniqueId, data)
    }

    private fun getGrindingSeed(bukkitPlayer: Player): Long {
        var seed = bukkitPlayer.persistentDataContainer.get(
            CustomCraftingSpigot.playerGrindingSeedKey,
            PersistentDataType.LONG
        )
        if (seed == null) {
            seed = Random.Default.nextLong()
            bukkitPlayer.persistentDataContainer.set(
                CustomCraftingSpigot.playerGrindingSeedKey,
                PersistentDataType.LONG,
                seed
            )
        }
        return seed
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.inventory is GrindstoneInventory) {
            recipeCache.invalidate(event.player.uniqueId)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        recipeCache.invalidate(event.player.uniqueId)
    }

}