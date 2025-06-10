package com.wolfyscript.customcrafting.spigot.recipes

import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.spigot.CustomCraftingSpigot
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toPreciseGlobal
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrap
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import com.wolfyscript.scafall.toAPI
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.block.Crafter
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.CrafterCraftEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.CrafterInventory
import org.bukkit.persistence.PersistentDataType

class CrafterListener(val customCrafting: CustomCraftingSpigot) : Listener {

    private val previousRecipeContainerKey = NamespacedKey("customcrafting", "previous_custom_recipe")

    @EventHandler
    fun onCraft(event: CrafterCraftEvent) {
        val block = event.block
        val state = block.state
        if (state !is Crafter) {
            return
        }
        val inventory = state.inventory as CrafterInventory

        val context = EvaluationContextImpl(null, block.location.toPreciseGlobal())
        val matrix = CraftingMatrixData.of(inventory.contents.map { it?.wrap() })
        val input = RecipeInput.CraftingRecipeInput.of(matrix)

        val previousRecipeKey =
            state.persistentDataContainer.get(previousRecipeContainerKey, PersistentDataType.STRING)?.let {
                Key.parse(it)
            }
        val previousRecipe = previousRecipeKey?.let { customCrafting.recipeManager.getRecipe(it) }

        val data = if (previousRecipe != null && previousRecipe is CustomRecipeCrafting) {
            previousRecipe.evaluate(input, context)
        } else {
            customCrafting.recipeManager.evaluateRecipesOfType(
                RecipeTypes.crafting,
                input,
                context
            )
        }

        if (data != null) {
            // Save the used recipe to not reiterate the next craft
            state.persistentDataContainer.set(
                previousRecipeContainerKey,
                PersistentDataType.STRING,
                previousRecipeKey.toString()
            )

            val inventory = state.snapshotInventory
            data.recipe.shrink(input, data, context, 1) { index, new ->
                inventory.setItem(index, new.unwrap())
            }
            // Now all calculations are done, so we can update the inventory
            Bukkit.getScheduler().runTask(customCrafting.bootstrap.plugin, Runnable {
                state.update(true)
            })
            return
        }

        val bukkitRecipe = event.recipe

        // We need placeholder recipes that simply use material choices, because otherwise we can get duplication issues and buggy behaviour like flickering.
        // Here we need to disable those placeholder recipes and check for a vanilla recipe the placeholder may override.
        if (bukkitRecipe.isPlaceholder() || bukkitRecipe.isDisplay()) {
            // Can't determine the vanilla recipe! We may need NMS for that in the future. For now simply override vanilla recipes.
            event.isCancelled = true
            return
        }

        // Check for custom recipe that overrides the vanilla recipe
        if (customCrafting.recipeManager.disabledRecipes.contains(bukkitRecipe.key.toAPI()) || customCrafting.recipeManager.getRecipe(bukkitRecipe.key.toAPI()) != null
        ) {
            // Recipe is disabled or it is a custom recipe!
            event.isCancelled = true
            return
        }

    }

    @EventHandler
    fun onCrafterInvClick(event: InventoryClickEvent) {
        if (event.inventory !is CrafterInventory) {
            return
        }

        event.inventory.holder?.let {
            if (it !is Crafter) {
                return
            }
            // Remove the previous recipe, because it may have changed
            it.persistentDataContainer.remove(previousRecipeContainerKey)
        }

    }

}