package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.resource.ResourceListener
import com.wolfyscript.customcrafting.resource.ResourceLoader
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.customcrafting.util.exportResource
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.verification.VerificationResult
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import java.io.File
import java.util.*

class RecipeManagerCommon(val customCrafting: CustomCraftingCommon) : RecipeManager, ResourceListener {

    val index: RecipeIndex = RecipeIndex()

    /**
     * Recipes can be loaded by other plugins. We keep track of which recipes CC registers, to not unload third-party recipes, for example, on a reload.
     */
    val recipesLoadedByCC: MutableSet<Key> = ObjectOpenHashSet()

    override val disabledRecipes: Set<Key>
        get() {
            return Collections.unmodifiableSet(backingDisabledRecipes)
        }
    val backingDisabledRecipes: MutableSet<Key> = ObjectOpenHashSet()

    val awaitingVerificationRecipes: MutableMap<Key, CustomRecipe<*, *>> = Object2ObjectOpenHashMap()
    val invalidRecipes: MutableList<VerificationResult<CustomRecipe<*, *>>> = mutableListOf()
    val scafall = ScafallProvider.get()

    val loadLock = Any()

    init {
        scafall.dependencyManager.onDependencyInitialized {
            synchronized(loadLock) {
                verifyRecipesAndLoad()
            }
        }
    }

    /**
     * Prepare everything before recipes are loaded.
     */
    override fun onPrepare(resourceLoader: ResourceLoader) {
        exportDefaults(resourceLoader)
    }

    private fun exportDefaults(resourceLoader: ResourceLoader) {
        customCrafting.logger.info("Exporting default recipes to $resourceLoader")
        val dir = "com/wolfyscript/customcrafting/recipes/default"
        listOf(
            "enchanted_golden_apple",
            "rotten_flesh_to_leather_smelting",
            "rotten_flesh_to_leather_smoking",
            "stick_to_torch_campfire",
            "stick_to_soul_torch_soul_campfire",
            "sus_stew_op",
            "upgrade_netherite_sword",
            "repair_with_amethyst",
            "disenchant_netherite_sword_custom",
            "leaves_selection_stonecutter"
        ).forEach {
            customCrafting.logger.info("  - recipe: $it")
            exportResource("$dir/$it.conf", File(resourceLoader.directory, "default/$it.conf"))
        }
    }

    /**
     * How recipes should be loaded on startup
     */
    override fun onInitialLoad(resourceLoader: ResourceLoader) {
        synchronized(loadLock) {
            resourceLoader.destinations.forEach { dest ->
                dest.load {
                    customCrafting.logger.info("  loaded recipe: ${it.key} -> ${it.recipe}")
                    awaitingVerificationRecipes[it.key] = it.recipe
                }
            }
        }
    }

    /**
     * How recipes should be loaded when reloaded at runtime.
     * This should run on a separate thread, async to the main thread.
     */
    override fun onReload(resourceLoader: ResourceLoader) {

    }

    /**
     * Finalize the loaded recipes (for which the dependencies are already available).
     * Verify them, add them to the manager, and remove any recipes that were previously loaded and no longer loaded. (important for reloads)
     */
    override fun onFinalize(resourceLoader: ResourceLoader) {
        synchronized(loadLock) {
            val previousLoaded = recipesLoadedByCC.toSet()
            recipesLoadedByCC.clear()

            verifyRecipesAndLoad()

            // Remove recipes that are no longer loaded
            val removed = previousLoaded.subtract(recipesLoadedByCC)
            if (removed.isNotEmpty()) {
                customCrafting.logger.info("Removing ${removed.size} recipes that are no longer loaded")

                for (recipeKey in removed) {
                    customCrafting.logger.info("  - $recipeKey")
                    removeRecipe(recipeKey)
                }
            }
        }
    }

    private fun verifyRecipesAndLoad() {
        customCrafting.logger.info("Verifying ${awaitingVerificationRecipes.size} recipes")
        for ((key, recipe) in awaitingVerificationRecipes) {
            // TODO: Verification
            customCrafting.recipeManager.updateRecipe(key, recipe)
        }
    }

    override fun <I : RecipeInput, D : RecipeEvaluationResult.Data, T : CustomRecipe<I, D>> evaluateRecipesOfType(
        type: RecipeType<T>,
        input: I,
        context: EvaluationContext,
    ): RecipeEvaluationResult<D, T>? {
        val recipes: Collection<RecipeReference<T>> = index.byType(type)
        for (recipe in recipes) {
            val data = recipe.value?.evaluate(input, context) ?: continue
            return RecipeEvaluationResultImpl(recipe, data)
        }
        return null
    }

    override fun disableRecipe(recipe: Key) {
        val ref = index.get(recipe)
        if (ref != null) {
            backingDisabledRecipes.add(recipe)
        }
    }

    override fun enableRecipe(key: Key) {
        backingDisabledRecipes.remove(key)
    }

    override fun isRecipeDisabled(key: Key): Boolean {
        return backingDisabledRecipes.contains(key)
    }

    fun registerRecipe(key: Key, recipe: CustomRecipe<*, *>) {
        if (key.namespace == Key.CUSTOMCRAFTING_NAMESPACE) {
            recipesLoadedByCC.add(key)
        }
        index.register(key, recipe)
    }

    override fun getRecipe(key: Key): CustomRecipe<*, *>? {
        return index.get(key)?.value
    }

    override fun removeRecipe(key: Key) {
        index.remove(key)
    }

    override fun updateRecipe(
        key: Key,
        recipe: CustomRecipe<*, *>,
    ) {
        if (index.byKey.containsKey(key)) {
            removeRecipe(key)
        }
        registerRecipe(key, recipe)
    }

}