package com.wolfyscript.customcrafting.spigotlike.recipes

import com.github.benmanes.caffeine.cache.Caffeine
import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeSmithing
import com.wolfyscript.customcrafting.core.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.core.recipes.RecipeTypes
import com.wolfyscript.customcrafting.core.recipes.SmithingUtils
import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.spigotlike.RecipeSeeds
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toPreciseGlobal
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toScafall
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrapSpigot
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.PrepareSmithingEvent
import org.bukkit.event.inventory.SmithItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.SmithingInventory
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.util.UUID
import kotlin.random.Random

class SmithingListener(val plugin: Plugin, val customCrafting: CustomCrafting) : Listener {

    val recipeCache =
        Caffeine.newBuilder().build<UUID, RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeSmithing>>()
    val collectsResult = Caffeine.newBuilder().build<UUID, Boolean>()

    private fun getSmithingSeed(bukkitPlayer: Player): Long {
        var seed = bukkitPlayer.persistentDataContainer.get(
            RecipeSeeds.playerSmithingSeedKey,
            PersistentDataType.LONG
        )
        if (seed == null) {
            seed = Random.nextLong()
            bukkitPlayer.persistentDataContainer.set(
                RecipeSeeds.playerSmithingSeedKey,
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

        if (resultStack != null && resultStack.type != Material.AIR) {
            // Check for disabled vanilla recipes
            if (Bukkit.getRecipesFor(resultStack).any {
                    customCrafting.server!!.recipeManager.isRecipeDisabled((it as Keyed).key.toScafall())
                }) {
                event.result = null
            }
        }

        val context = EvaluationContextImpl((event.view.player as Player).wrap(), inventory.location?.toPreciseGlobal())
        val templateStack = inventory.getItem(0)
        val baseStack = inventory.getItem(1)
        val additionStack = inventory.getItem(2)

        val data = customCrafting.server!!.recipeManager.evaluateRecipesOfType(
            RecipeTypes.smithing.resolveOrThrow(),
            RecipeInput.SmithingRecipeInput.of(
                templateStack?.wrap(), baseStack?.wrap(), additionStack?.wrap()
            ),
            context
        )

        if (data != null) {
            val recipe = data.recipe.value ?: return

            recipeCache.put(event.view.player.uniqueId, data)

            val endResult = recipe.result.compute(
                data,
                context, Random(getSmithingSeed(event.view.player as Player))
            ).unwrapSpigot()

            if (baseStack == null) {
                event.result = null
                return
            }

            SmithingUtils.copyDataComponentsTo(baseStack.wrap(), endResult.wrap(), recipe.copyOptions!!)
            return
        }

        // No recipe was matched
        if (event.result == null || event.result!!.type == Material.AIR) {
            return
        }
        if (inventory.recipe == null || inventory.recipe!!.isPlaceholder() || inventory.recipe!!.isDisplay()) {
            event.result = null
        }

    }

    @EventHandler
    fun onCollectResult(event: SmithItemEvent) {
        val inventory = event.clickedInventory
        if (inventory == null || inventory !is SmithingInventory) {
            return
        }
        if (event.slot != 3 || event.currentItem == null || event.currentItem!!.type == Material.AIR) {
            return
        }

        val player = event.whoClicked as Player
        val data = recipeCache.getIfPresent(player.uniqueId) ?: return
        val recipe = data.recipe.value ?: return

        val resultStack = inventory.result
        if (resultStack == null || resultStack.type == Material.AIR) {
            return
        }

        event.result = Event.Result.DENY  // Cancel the event to prevent vanilla ingredient consumption

        if (event.isShiftClick) {
            if (event.view.bottomInventory.addItem(resultStack).isNotEmpty()) {
                return
            }
        }
        // A quick implementation to collect the result. Things like moving the item to the hotbar won't work!
        if (event.cursor.type == Material.AIR) {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                event.view.setCursor(resultStack)
            })
        } else if (event.cursor.isSimilar(resultStack)) {
            if (event.cursor.amount + resultStack.amount > event.cursor.maxStackSize) {
                return // does not fit on cursor. cancel recipe
            }
            Bukkit.getScheduler().runTask(plugin, Runnable {
                event.view.cursor.amount = event.cursor.amount + resultStack.amount
            })
        }

        val context = EvaluationContextImpl((event.whoClicked as Player).wrap(), inventory.location?.toPreciseGlobal())

        recipe.result.runActions(context, 1)

        // Use setContents so we batch slot updates and only cause one update, so PrepareSmithingEvent is just called once instead of three times.
        inventory.contents = arrayOf(
            shrinkIngredient(inventory, 0, context, data),
            shrinkIngredient(inventory, 1, context, data),
            shrinkIngredient(inventory, 2, context, data)
        )

        player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, 1f, 1f)

        // Reset seed for next result generation
        player.persistentDataContainer.set(
            RecipeSeeds.playerSmithingSeedKey,
            PersistentDataType.LONG,
            Random.nextLong()
        )
        recipeCache.invalidate(player.uniqueId)
    }

    private fun shrinkIngredient(
        inventory: SmithingInventory,
        index: Int,
        context: EvaluationContextImpl,
        data: RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeSmithing>,
    ): ItemStack {
        return data.data.bySlot(index)?.let { ingredientData ->
            inventory.getItem(index)?.let {
                ingredientData.selectedIngredient.shrink(
                    it.wrap(),
                    1,
                    ingredientData.matchedItemStackRef,
                    context,
                    data
                )
            }?.unwrapSpigot()
        } ?: ItemStack(Material.AIR)
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