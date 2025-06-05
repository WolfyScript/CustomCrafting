package com.wolfyscript.customcrafting.spigot.recipes

import com.github.benmanes.caffeine.cache.Caffeine
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.spigot.CustomCraftingSpigot
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toPreciseGlobal
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrap
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.view.AnvilView
import org.bukkit.persistence.PersistentDataType
import java.util.UUID
import kotlin.random.Random

class AnvilListener(val customCrafting: CustomCrafting) : Listener {

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
        val input = RecipeInput.RepairingRecipeInput.of(base.wrap(), addition?.wrap())

        val data = customCrafting.recipeManager.evaluateRecipesOfType(RecipeTypes.repairing, input, context)
        if (data == null) {
            // no custom recipe. Vanilla behaviour
            return
        }
        recipeCache.put(player.uniqueId, data)
        val process = data.recipe.process

        val result = process.compute(data, context, Random(getRepairingSeed(player)))
        event.result = result.unwrap()

        if (process is CustomRecipeRepairing.RepairProcess.FixedResult) {
            process.cost?.let {
                event.view.repairCost = it
            }
        }

    }

    @EventHandler
    fun onTake(event: InventoryClickEvent) {
        val inventory = event.inventory
        val view = event.view
        if (inventory !is AnvilInventory || view !is AnvilView) {
            return
        }
        val base = inventory.getItem(1)
        if (base == null || base.type == Material.AIR) {
            return
        }
        val result = event.currentItem
        if (event.slot != 2 || result == null || result.type == Material.AIR ) {
            return
        }
        val player = event.whoClicked as Player
        val data = recipeCache.getIfPresent(player.uniqueId)
        if (data == null) {
            return
        }

        val context = EvaluationContextImpl(player.wrap(), inventory.location?.toPreciseGlobal())
        val input = RecipeInput.RepairingRecipeInput.of(base.wrap(), inventory.getItem(1)?.wrap())

        val cursor = event.cursor

        // TODO: Cursor pickup


        val process = data.recipe.process
        if (process is CustomRecipeRepairing.RepairProcess.FixedResult) {
            process.result.runActions(context, 1)
        }

        val location = inventory.location
        if (location != null && location.world != null) {
            // TODO: damage anvil block
            location.world.playEffect(location, Effect.ANVIL_USE, 0)
        }

        if (player.level >= view.repairCost) {
            player.level = player.level - view.repairCost
        }
        event.currentItem = null

        data.bySlot(0)?.let {
            inventory.getItem(0)?.apply {
                amount -= it.matchedItemStackRef.amount
            }
        }

        data.bySlot(1)?.let {
            inventory.getItem(1)?.apply {
                amount -= it.matchedItemStackRef.amount
            }
        }

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