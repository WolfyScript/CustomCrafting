package com.wolfyscript.customcrafting.spigotlike.recipes

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.getRecipeTyped
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toBlockPos
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toPreciseGlobal
import com.wolfyscript.scafall.spigot.api.wrappers.utils.toScafall
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrapSpigot
import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.block.Crafter
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.CrafterCraftEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.CrafterInventory
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import kotlin.random.Random

class CrafterListener(val plugin: Plugin, val customCrafting: CustomCraftingCommon) : Listener {

    private val previousRecipeContainerKey = NamespacedKey("customcrafting", "previous_custom_recipe")

    @EventHandler
    fun onCraft(event: CrafterCraftEvent) {
        val block = event.block
        val state = block.state
        if (state !is Crafter) {
            return
        }
        val inventory = state.inventory

        val context = EvaluationContextImpl(null, block.location.toPreciseGlobal(), block.location.toBlockPos(), state.wrap())
        val matrix = CraftingMatrixData.of(inventory.contents.map { it?.wrap() })
        val input = RecipeInput.CraftingRecipeInput.of(matrix)

        val previousRecipeKey =
            state.persistentDataContainer.get(previousRecipeContainerKey, PersistentDataType.STRING)?.let {
                try {
                    Key.parse(it)
                } catch (_: Exception) {
                    null
                }
            }
        val previousRecipe = previousRecipeKey?.let {
            customCrafting.recipeManager.getRecipeTyped(
                it,
                RecipeTypes.crafting.resolveOrThrow()
            )
        }

        val data = if (previousRecipe != null) {
            previousRecipe.value?.evaluate(input, context)?.let { RecipeEvaluationResultImpl(previousRecipe, it) }
        } else {
            customCrafting.recipeManager.evaluateRecipesOfType(
                RecipeTypes.crafting.resolveOrThrow(),
                input,
                context
            )
        }
        val recipe = data?.recipe?.value

        if (recipe != null) {
            // Save the used recipe to not reiterate the next craft
            state.persistentDataContainer.set(
                previousRecipeContainerKey,
                PersistentDataType.STRING,
                data.recipe.key.toString()
            )

            val inventory = state.snapshotInventory
            recipe.shrink(input, data, context, 1) { index, new ->
                inventory.setItem(index, new.unwrapSpigot())
            }
            recipe.result.runActions(context)
            val resultStack = recipe.result.compute(data, context, Random)
            event.result = resultStack.unwrapSpigot()

            // Now all calculations are done, so we can update the inventory
            Bukkit.getScheduler().runTask(plugin, Runnable {
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
        if (customCrafting.recipeManager.isRecipeDisabled(bukkitRecipe.key.toScafall()) ||
            customCrafting.recipeManager.getRecipe(bukkitRecipe.key.toScafall()) != null) {
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