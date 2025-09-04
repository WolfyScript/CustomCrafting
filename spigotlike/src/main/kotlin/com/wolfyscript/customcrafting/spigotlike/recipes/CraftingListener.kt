package com.wolfyscript.customcrafting.spigotlike.recipes

import com.github.benmanes.caffeine.cache.Caffeine
import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.EvaluationContext
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.spigotlike.RecipeSeeds
import com.wolfyscript.customcrafting.spigotlike.collectResultAndRunActions
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toPreciseGlobal
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toScafall
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrapSpigot
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.util.UUID
import kotlin.random.Random

class CraftingListener(val plugin: Plugin, val customCrafting: CustomCraftingCommon) : Listener {

    val recipeManager = customCrafting.recipeManager

    /**
     * Used to cache the state of the crafting grid for a player.
     * Entries are invalidated when the player disconnects, closes the inv, or crafts a recipe.
     */
    val craftingDataCache = Caffeine.newBuilder().weakKeys().build<UUID, RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting>>()

    /**
     * Used to cache the matrix state of either the crafting table or player inventory crafting grid.
     * Since only one can be used at once, we don't need to distinguish between them.
     */
    val matrixDataCache = Caffeine.newBuilder().weakKeys().build<UUID, CraftingMatrixData>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onCraft(event: InventoryClickEvent) {
        val inventory = event.clickedInventory
        if (inventory !is CraftingInventory) return
        if (event.slot != 0) {
            // We only care about the result slot. Ignore the rest
            return
        }
        val resultItem: ItemStack? = inventory.result
        val cursor = event.cursor
        if (resultItem?.type == Material.AIR || (cursor.type != Material.AIR && !cursor.isSimilar(resultItem) && !event.isShiftClick)) {
            // Make sure we don't consume the recipe if there is no result or the cursor cannot pick up the item
            event.isCancelled = true
            return
        }

        val matrixData = matrixDataCache.getIfPresent(event.whoClicked.uniqueId)
            ?: return // The result was picked up before CustomCrafting calculated the matrix data.
        val craftingData = craftingDataCache.getIfPresent(event.whoClicked.uniqueId)
            ?: return // Not a custom recipe
        val recipe = craftingData.recipe.value
            ?: return // TODO: special handling. Recipe that was evaluated, has been removed in the meantime

        event.isCancelled = true
        val player = event.whoClicked as Player
        if (event.isShiftClick || cursor.type == Material.AIR || cursor.amount + resultItem!!.amount <= cursor.maxStackSize) {
            matrixDataCache.invalidate(player.uniqueId)
            craftingDataCache.invalidate(player.uniqueId)

            val context: EvaluationContext =
                EvaluationContextImpl(player.wrap(), event.inventory.location?.toPreciseGlobal())

            // At this point do not change the inventory! Because that would call the PrepareItemCraftEvent, invalidating the recipe and preventing consumption of the recipe!
            val count: Int = collectResult(event, player, craftingData, matrixData, context)
            val input = RecipeInput.CraftingRecipeInput.of(matrixData)

            val matrix: Array<ItemStack?> = Array(inventory.matrix.size) { null }
            recipe.shrink(input, craftingData, context, count) { index, new ->
                matrix[index] = new.unwrapSpigot()
            }
            // Now all calculations are done, so we can update the inventory
            inventory.matrix = matrix
            player.updateInventory()
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPreCraft(e: PrepareItemCraftEvent) {
        val player = e.view.player as Player
        try {
            val matrix = CraftingMatrixData.of(e.inventory.matrix.map { it?.wrap() }.toList())
            val input = RecipeInput.CraftingRecipeInput.of(matrix)
            matrixDataCache.put(player.uniqueId, matrix)

            val block = e.inventory.location?.block ?: player.location.block
            val context: EvaluationContext = EvaluationContextImpl(player.wrap(), block.location.toPreciseGlobal())
            val resultStack = recipeManager.evaluateRecipesOfType(RecipeTypes.crafting.resolveOrThrow(), input, context)?.let {
                craftingDataCache.put(player.uniqueId, it)
                it.recipe.value?.result?.compute(it, context, Random(getCraftSeed(player)))
            }

            if (resultStack != null) {
                e.inventory.result = resultStack.unwrapSpigot()
                Bukkit.getScheduler().runTask(plugin, Runnable { player.updateInventory() })
            } else {
                val recipe = e.recipe
                // No valid custom recipes found
                if (recipe !is Keyed) return

                // We need placeholder recipes that simply use material choices, because otherwise we can get duplication issues and buggy behaviour like flickering.
                // Here we need to disable those placeholder recipes and check for a vanilla recipe the placeholder may override.
                if (recipe.isPlaceholder() || recipe.isDisplay()) {
                    // TODO: Can't determine the vanilla recipe! We may need NMS for that in the future. For now simply override vanilla recipes.
                    e.inventory.result = ItemStack(Material.AIR)
                    Bukkit.getScheduler().runTask(plugin, Runnable { player.updateInventory() })
                    return
                }

                val recipeKey = recipe.key.toScafall()
                //Check for custom recipe that overrides the vanilla recipe
                if (recipeManager.disabledRecipes.contains(recipeKey) || customCrafting.recipeManager.getRecipe(
                        recipeKey
                    ) != null
                ) {
                    //Recipe is disabled or it is a custom recipe!
                    e.inventory.result = ItemStack(Material.AIR)
                    Bukkit.getScheduler().runTask(plugin, Runnable { player.updateInventory() })
                    return
                }

                //At this point the vanilla recipe is valid and can be crafted
                Bukkit.getScheduler().runTask(plugin, Runnable { player.updateInventory() })
            }
        } catch (ex: Exception) {
            customCrafting.logger.error("-------- [Error occurred while crafting Recipe!] --------")
            ex.printStackTrace()
            customCrafting.logger.error("-------- [Error occurred while crafting Recipe!] --------")
            craftingDataCache.invalidate(player.uniqueId)
            matrixDataCache.invalidate(player.uniqueId)
            e.inventory.result = ItemStack(Material.AIR)
        }
    }

    @EventHandler
    fun onCloseInv(event: InventoryCloseEvent) {
        craftingDataCache.invalidate(event.player.uniqueId)
        matrixDataCache.invalidate(event.player.uniqueId)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        craftingDataCache.invalidate(event.player.uniqueId)
        matrixDataCache.invalidate(event.player.uniqueId)
    }

    fun collectResult(
        event: InventoryClickEvent,
        bukkitPlayer: Player,
        craftingData: RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting>,
        matrixData: CraftingMatrixData,
        context: EvaluationContext,
    ): Int {
        if (event.clickedInventory == null) return 0
        val recipeResult = craftingData.recipe.value?.result ?: return 0
        return collectResultAndRunActions(event, bukkitPlayer.inventory, craftingData, recipeResult, matrixData.items, context, Random(getCraftSeed(bukkitPlayer)))
    }

    fun getCraftSeed(bukkitPlayer: Player): Long {
        var seed = bukkitPlayer.persistentDataContainer.get(
            RecipeSeeds.playerCraftingSeedKey,
            PersistentDataType.LONG
        )
        if (seed == null) {
            seed = Random.nextLong()
            bukkitPlayer.persistentDataContainer.set(
                RecipeSeeds.playerCraftingSeedKey,
                PersistentDataType.LONG,
                seed
            )
        }
        return seed
    }

}