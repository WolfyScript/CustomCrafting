package com.wolfyscript.customcrafting.spigot.recipes

import com.github.benmanes.caffeine.cache.Caffeine
import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.repair.CombineProcess
import com.wolfyscript.customcrafting.spigot.CustomCraftingSpigot
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toPreciseGlobal
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrap
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import net.minecraft.world.entity.Entity
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.view.AnvilView
import org.bukkit.persistence.PersistentDataType
import java.util.UUID
import kotlin.random.Random

class AnvilListener(val customCrafting: CustomCraftingSpigot) : Listener {

    private val recipeCache = Caffeine.newBuilder().build<UUID, RecipeData<CustomRecipeRepairing>>()

    @EventHandler
    fun onPrepare(event: PrepareAnvilEvent) {
        val inventory = event.inventory
        val player = event.view.player as Player

        val base = inventory.getItem(0) ?: return
        if (base.type == Material.AIR) {
            return
        }
        val addition = inventory.getItem(1)

        val context = EvaluationContextImpl(player.wrap(), inventory.location?.toPreciseGlobal())
        val input = RecipeInput.RepairingRecipeInput.of(base.wrap(), addition?.wrap(), event.view.renameText)

        val data = customCrafting.recipeManager.evaluateRecipesOfType(RecipeTypes.repairing.resolveOrThrow(), input, context)
        if (data == null || data !is RecipeData.RepairingRecipeData) {
            // no custom recipe. Vanilla behaviour
            return
        }
        recipeCache.put(player.uniqueId, data)
        val process = data.recipe.process

        val result = process.compute(data, input, context, Random(getRepairingSeed(player)))
        event.result = result.unwrap()

        val correctCost = event.view.repairCost
        // Bukkit decided to set the repair cost of the anvil menu to -1 after the event call.
        // This bypasses it by setting it back to the proper repair cost, that we just set
        Bukkit.getScheduler().runTaskLater(customCrafting.bootstrap.plugin, Runnable {
            event.view.repairCost = correctCost
        }, 2)
    }

    @EventHandler
    fun onTake(event: InventoryClickEvent) {
        val inventory = event.inventory
        val view = event.view
        if (inventory !is AnvilInventory || view !is AnvilView) {
            return
        }
        val base = inventory.getItem(0)
        if (base == null || base.type == Material.AIR) {
            return // The base item cannot be emtpy!
        }
        val resultStack = event.currentItem
        if (event.slot != 2 || resultStack == null || resultStack.type == Material.AIR ) {
            return
        }
        val player = event.whoClicked as Player
        val data = recipeCache.getIfPresent(player.uniqueId)
        if (data == null || data !is RecipeData.RepairingRecipeData) {
            return
        }
        event.result = Event.Result.DENY // Deny the click event, we do our own calculations
        if (player.gameMode ==  GameMode.CREATIVE) {
            player.level += view.repairCost
        }
        if (player.level < view.repairCost) {
            return // The player level may have changed, making the recipe invalid
        }

        val cursor = event.cursor

        // A quick implementation to collect the result.
        if (event.isShiftClick) {
            if (event.view.bottomInventory.addItem(resultStack).isNotEmpty()) {
                return
            }
        }
        if (cursor.type == Material.AIR) {
            Bukkit.getScheduler().runTask(customCrafting.bootstrap.plugin, Runnable {
                event.view.setCursor(resultStack)
            })
        } else if (cursor.isSimilar(resultStack)) {
            if (cursor.amount + resultStack.amount > cursor.maxStackSize) {
                // TODO: try and put item into inventory
                return // does not fit on the cursor. cancel recipe processing.
            }
            Bukkit.getScheduler().runTask(customCrafting.bootstrap.plugin, Runnable {
                // since this is called next tick, the cursor might have changed, so use the latest
                event.view.cursor.amount = event.view.cursor.amount + resultStack.amount
            })
        }

        // At this point, the result was successfully picked up and all requirements are satisfied.
        // Continue to process level, actions, ingredients, etc.

        player.level = player.level - view.repairCost

        val context = EvaluationContextImpl(player.wrap(), inventory.location?.toPreciseGlobal())

        val process = data.recipe.process
        if (process is CombineProcess.FixedResult) {
            process.result.runActions(context, 1)
        }

        val location = inventory.location
        if (location != null && location.world != null) {
            // Mirror the vanilla behaviour of damaging the Anvil
            if (player.gameMode != GameMode.CREATIVE && Entity.SHARED_RANDOM.nextFloat() < 0.12) {
                // TODO: In Paper we could call the AnvilDamageEvent here for better compatibility with other plugins that may use it
                val block = location.block
                block.type = when (block.type) {
                    block.type -> Material.CHIPPED_ANVIL
                    block.type -> Material.DAMAGED_ANVIL
                    else -> Material.AIR
                }
            }

            location.world.playEffect(location, Effect.ANVIL_USE, 0)
        }

        event.currentItem = null

        // TODO: Craft remains!
        data.bySlot(0)?.let {
            inventory.getItem(0)?.apply {
                amount -= it.matchedItemStackRef.amount
            }
        }

        data.bySlot(1)?.let {
            inventory.getItem(1)?.apply {
                amount -= it.matchedItemStackRef.amount * (data.itemRepairCost ?: 1)
            }
        }

        // By this point, the recipe was processed, levels and ingredients consumed,
        // Now clear the cache
        recipeCache.invalidate(player.uniqueId)
        player.persistentDataContainer.set(CustomCraftingSpigot.playerRepairingSeedKey, PersistentDataType.LONG, Random.Default.nextLong())
        // and reset the cost, as vanilla would do normally
        view.repairCost = -1
    }

    fun getRepairingSeed(bukkitPlayer: Player): Long {
        var seed = bukkitPlayer.persistentDataContainer.get(
            CustomCraftingSpigot.playerRepairingSeedKey,
            PersistentDataType.LONG
        )
        if (seed == null) {
            seed = Random.Default.nextLong()
            bukkitPlayer.persistentDataContainer.set(
                CustomCraftingSpigot.playerRepairingSeedKey,
                PersistentDataType.LONG,
                seed
            )
        }
        return seed
    }

}