package com.wolfyscript.customcrafting.spigot.recipes

import com.github.benmanes.caffeine.cache.Caffeine
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.spigot.CustomCraftingSpigot
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.spigot.api.wrappers.utils.*
import com.wolfyscript.scafall.toAPI
import com.wolfyscript.scafall.wrappers.world.ScafallBlockPos
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.Furnace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExpEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.event.inventory.FurnaceStartSmeltEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.floor
import kotlin.random.Random

class FurnaceListener(val customCrafting: CustomCrafting) : Listener {

    private val backingRecipesUsedKey = NamespacedKey("customcrafting", "backing_recipes_used")
    private val customRecipesUsedKey = NamespacedKey("customcrafting", "recipes_used")

    private val recipeCache = Caffeine.newBuilder().build<ScafallBlockPos, CookingRecipeCache>()

    @EventHandler
    fun onStartSmelt(event: FurnaceStartSmeltEvent) {
        val bukkitRecipe = event.recipe
        val source = event.source
        val block = event.block
        val blockPos = block.location.toBlockPos()
        recipeCache.invalidate(blockPos)

        val customBackingRecipe = bukkitRecipe.isPlaceholder() || bukkitRecipe.isDisplay()

        val input = RecipeInput.CookingRecipeInput.of(source.wrap(), null)
        val context = EvaluationContextImpl(null, block.location.toPreciseGlobal())

        val customRecipeData = customCrafting.recipeManager.evaluateRecipesOfType(RecipeTypes.cooking, input, context)
        if (customRecipeData != null) {
            event.totalCookTime = customRecipeData.recipe.processing.processingTime

            recipeCache.put(
                blockPos,
                CookingRecipeCache(customRecipeData, bukkitRecipe.key, customBackingRecipe)
            )
            return
        }

        // No custom recipe found, but it should be cancelled when the bukkit recipe is a placeholder or display recipe
        recipeCache.put(
            blockPos,
            CookingRecipeCache(null, bukkitRecipe.key, customBackingRecipe)
        )
    }

    @EventHandler
    fun onSmelt(event: FurnaceSmeltEvent) {
        val block = event.block
        val blockPos = block.location.toBlockPos()

        recipeCache.getIfPresent(blockPos)?.let { cache ->
            if (cache.recipeData != null) {
                val inventory = (block.state as Furnace).inventory
                val source = inventory.smelting ?: return
                val resultStack = inventory.result

                updateRecipeExperience(
                    block,
                    event.recipe?.key,
                    customCrafting.registries.customRecipes.getKey(cache.recipeData.recipe)!!
                )

                val result = cache.recipeData.recipe.result

                val context = EvaluationContextImpl(null, block.location.toPreciseGlobal())
                val pickedStack = result.compute(
                    cache.recipeData,
                    context,
                    Random(getCookingSeed(block.state as Furnace))
                ).unwrap()

                //Need to set the result to air to bypass the vanilla result computation (See net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity#burn).
                event.result = ItemStack(Material.AIR)
                if (resultStack != null) {
                    if (!pickedStack.isSimilar(resultStack)) { // Cannot smelt item, does not match item in the result slot
                        event.isCancelled = true
                        return
                    }

                    val newAmount = resultStack.amount + pickedStack.amount
                    if (newAmount > resultStack.maxStackSize) { // Cannot smelt item, does not fit
                        event.isCancelled = true
                        return
                    }
                    resultStack.amount = newAmount
                } else {
                    inventory.result = pickedStack
                }

                cache.recipeData.bySlot(0)?.let {
                    val matchedRef = it.matchedItemStackRef
                    source.apply {
                        amount = amount - matchedRef.amount
                    }
                    // TODO: Remains
                }

                result.runActions(context)

                // Successfully smelted result, pick a new seed to pick the next random result
                (block.state as Furnace).persistentDataContainer.set(
                    CustomCraftingSpigot.cookingSeedKey,
                    PersistentDataType.LONG,
                    Random.Default.nextLong()
                )
                recipeCache.invalidate(blockPos)
                return
            }

            if (cache.customBackingRecipe) {
                event.isCancelled = true
            }
        }
    }

    private fun getCookingSeed(state: Furnace): Long {
        var seed = state.persistentDataContainer.get(
            CustomCraftingSpigot.cookingSeedKey,
            PersistentDataType.LONG
        )
        if (seed == null) {
            seed = Random.Default.nextLong()
            state.persistentDataContainer.set(
                CustomCraftingSpigot.cookingSeedKey,
                PersistentDataType.LONG,
                seed
            )
        }
        return seed
    }

    /* **************************************************************************************** *
     * ## Experience Handling
     *
     * Custom Recipe usages are stored in a separate NBT tag of the tile entity, because
     * they may override vanilla recipes and therefore would not be included in the vanilla xp calculation.
     *
     * Custom calculation is required to drop the correct amount of xp when collecting the result item.
     *
     * **************************************************************************************** */

    /**
     * Increments the used count for both the actual CustomRecipe and the backing Bukkit (Vanilla) recipe that Minecraft used.
     * This keeps track of how many Bukkit (Vanilla) recipes are used for CustomRecipes, so they can be subtracted from the experience to drop,
     * because the Bukkit (Vanilla) recipe was overridden by the CustomRecipe.
     *
     * @param furnace The Furnace block
     * @param backingRecipe The actual bukkit (vanilla) recipe that is present in the event
     * @param recipeKey The custom recipe key
     */
    private fun updateRecipeExperience(
        furnace: Block,
        backingRecipe: NamespacedKey?,
        recipeKey: Key,
    ) {
        val blockState = furnace.state as Furnace

        val rootContainer = blockState.persistentDataContainer
        if (backingRecipe != null) {
            // Increment usages for Bukkit (Vanilla) recipes, so we can subtract those later
            var usedBackingRecipes = rootContainer.get(
                backingRecipesUsedKey,
                PersistentDataType.TAG_CONTAINER
            )
            if (usedBackingRecipes == null) {
                usedBackingRecipes = rootContainer.adapterContext.newPersistentDataContainer()
            }
            val recipeCount: Int =
                usedBackingRecipes.getOrDefault(backingRecipe, PersistentDataType.INTEGER, 0)
            usedBackingRecipes.set(backingRecipe, PersistentDataType.INTEGER, recipeCount + 1)
        }

        // Increment custom recipe usages
        var usedRecipes = rootContainer.get(
            customRecipesUsedKey,
            PersistentDataType.TAG_CONTAINER
        )
        if (usedRecipes == null) {
            usedRecipes = rootContainer.adapterContext.newPersistentDataContainer()
        }
        val bukkitKey = recipeKey.toSpigot()
        val amount: Int = usedRecipes.getOrDefault(bukkitKey, PersistentDataType.INTEGER, 0)
        usedRecipes.set(bukkitKey, PersistentDataType.INTEGER, amount + 1)

        // Update root container
        rootContainer.set(
            customRecipesUsedKey,
            PersistentDataType.TAG_CONTAINER,
            usedRecipes
        )
        blockState.update()
    }

    @EventHandler
    fun onCollectExperience(event: BlockExpEvent) {
        val state = event.block.state

        if (state is Furnace) {
            val rootContainer = state.persistentDataContainer
            var expToDrop = event.expToDrop

            // Subtract backing recipe xp from the total experience
            val vanillaRecipesUsed = state.recipesUsed
            val usedBackingRecipes = rootContainer.get(backingRecipesUsedKey, PersistentDataType.TAG_CONTAINER)?.let {
                it.keys.associateWith { key -> it.get(key, PersistentDataType.INTEGER) ?: 0 }
            } ?: emptyMap()

            for ((recipe, _) in vanillaRecipesUsed) {
                usedBackingRecipes[recipe.key]?.let {
                    expToDrop -= floor(it * recipe.experience).toInt()
                }
            }

            // Add custom recipe experience
            val usedRecipes = rootContainer.get(customRecipesUsedKey, PersistentDataType.TAG_CONTAINER)?.let {
                it.keys.associateWith { key -> it.get(key, PersistentDataType.INTEGER) ?: 0 }
                    .mapKeys { (key, _) -> key.toAPI() }
            } ?: emptyMap()

            for ((key, count) in usedRecipes) {
                customCrafting.recipeManager.getRecipe(key)?.let { recipe ->
                    if (recipe !is CustomRecipeCooking) {
                        return@let
                    }
                    expToDrop += floor(count * recipe.xp).toInt()
                }
            }

            // Update exp for event
            event.expToDrop = expToDrop

            // Then clear the recipe usages
            rootContainer.set(customRecipesUsedKey, PersistentDataType.TAG_CONTAINER, rootContainer.adapterContext.newPersistentDataContainer())
            rootContainer.set(backingRecipesUsedKey, PersistentDataType.TAG_CONTAINER, rootContainer.adapterContext.newPersistentDataContainer())
            state.update()
        }
    }

}

data class CookingRecipeCache(
    val recipeData: RecipeData<CustomRecipeCooking>?,
    val bukkitRecipe: NamespacedKey,
    val customBackingRecipe: Boolean,
)