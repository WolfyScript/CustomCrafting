package com.wolfyscript.customcrafting.spigot.recipes

import com.github.benmanes.caffeine.cache.Caffeine
import com.wolfyscript.customcrafting.recipes.CustomRecipeSmithing
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.SmithingUtils
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.spigot.CustomCraftingSpigot
import com.wolfyscript.scafall.adventure.toAPI
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toPreciseGlobal
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrap
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareSmithingEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.SmithingInventory
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.random.Random

class SmithingListener(val customCrafting: CustomCraftingSpigot) : Listener {

    val recipeCache = Caffeine.newBuilder().build<UUID, RecipeData<CustomRecipeSmithing>>()

    private fun getSmithingSeed(bukkitPlayer: Player): Long {
        var seed = bukkitPlayer.persistentDataContainer.get(
            CustomCraftingSpigot.playerSmithingSeedKey,
            PersistentDataType.LONG
        )
        if (seed == null) {
            seed = Random.Default.nextLong()
            bukkitPlayer.persistentDataContainer.set(
                CustomCraftingSpigot.playerSmithingSeedKey,
                PersistentDataType.LONG,
                seed
            )
        }
        return seed
    }

    @EventHandler
    fun onPrepare(event: PrepareSmithingEvent) {
        val inventory = event.inventory
        val resultStack = event.result

        recipeCache.invalidate(event.view.player.uniqueId)

        if (resultStack != null && !resultStack.isEmpty) {
            // Check for disabled vanilla recipes
            if (Bukkit.getRecipesFor(resultStack).any {
                    customCrafting.recipeManager.disabledRecipes.contains((it as Keyed).key.toAPI())
                }) {
                event.result = null
            }
        }

        val context = EvaluationContextImpl((event.view.player as Player).wrap(), inventory.location?.toPreciseGlobal())
        val templateStack = inventory.getItem(0)
        val baseStack = inventory.getItem(1)
        val additionStack = inventory.getItem(2)

        val data = customCrafting.recipeManager.evaluateRecipesOfType(
            RecipeTypes.smithing,
            RecipeInput.SmithingRecipeInput.of(
                templateStack?.wrap(), baseStack?.wrap(), additionStack?.wrap()
            ),
            context
        )
        if (data != null) {
            recipeCache.put(event.view.player.uniqueId, data)

            val endResult = data.recipe.result.compute(
                data,
                context, Random(getSmithingSeed(event.view.player as Player))
            ).unwrap()

            if (data.recipe.copyOptions == null) {
                // Take the base item and just change the material.
                if (baseStack == null) {
                    event.result = null
                    return
                }
                event.result = baseStack.clone().apply {
                    type = endResult.type
                    amount = endResult.amount
                }
            } else {
                if (baseStack == null) {
                    event.result = null
                    return
                }
                SmithingUtils.copyDataComponentsTo(baseStack.wrap(), endResult.wrap(), data.recipe.copyOptions!!.preserveComponents)
                event.result = endResult
            }
            return
        }

        // No recipe was matched
        if (event.result == null || event.result!!.isEmpty) {
            return
        }
        if (inventory.recipe == null || inventory.recipe!!.isPlaceholder() || inventory.recipe!!.isDisplay()) {
            event.result = null
        }

    }

    @EventHandler
    fun onCollectResult(event: InventoryClickEvent) {
        val inventory = event.clickedInventory
        if (inventory == null || inventory !is SmithingInventory) {
            return
        }
        if (event.slotType != InventoryType.SlotType.RESULT || event.currentItem == null || event.currentItem!!.isEmpty) {
            return
        }

        val player = event.whoClicked as Player
        val action = event.action

        val data = recipeCache.getIfPresent(player.uniqueId)
        if (data == null) {
            return // Vanilla recipe
        }

        val resultStack = inventory.result
        if (resultStack == null || resultStack.isEmpty) {
            return
        }

        event.isCancelled = true // Cancel the event to prevent vanilla ingredient consumption

        if (event.isShiftClick) {
            if (event.view.bottomInventory.addItem(resultStack).isNotEmpty()) {
                return
            }
        }
        // A quick implementation to collect the result. Things like moving the item to the hotbar won't work!
        if (event.cursor.type == Material.AIR) {
            Bukkit.getScheduler().runTask(customCrafting.bootstrap.plugin, Runnable {
                event.view.setCursor(resultStack)
            })
        } else if (event.cursor.isSimilar(resultStack)) {
            if (event.cursor.amount + resultStack.amount > event.cursor.maxStackSize) {
                return // does not fit on cursor. cancel recipe
            }
            Bukkit.getScheduler().runTask(customCrafting.bootstrap.plugin, Runnable {
                event.view.cursor.amount = event.cursor.amount + resultStack.amount
            })
        }

        val context = EvaluationContextImpl((event.whoClicked as Player).wrap(), inventory.location?.toPreciseGlobal())

        data.recipe.result.runActions(context, 1)

        // TODO: craft remains
        data.bySlot(0)?.let {
            val templateStack = inventory.getItem(0) ?: ItemStack(Material.AIR)
            templateStack.amount = templateStack.amount - it.matchedItemStackRef.amount
        }
        data.bySlot(1)?.let {
            val baseStack = inventory.getItem(1) ?: ItemStack(Material.AIR)
            baseStack.amount = baseStack.amount + it.matchedItemStackRef.amount
        }
        data.bySlot(2)?.let {
            val additionStack = inventory.getItem(2) ?: ItemStack(Material.AIR)
            additionStack.amount = additionStack.amount + it.matchedItemStackRef.amount
        }

        // Reset seed for next result generation
        player.persistentDataContainer.set(CustomCraftingSpigot.playerSmithingSeedKey, PersistentDataType.LONG, Random.Default.nextLong())
        recipeCache.invalidate(player.uniqueId)
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.inventory is SmithingInventory) {
            recipeCache.invalidate(event.player.uniqueId)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        recipeCache.invalidate(event.player.uniqueId)
    }

}