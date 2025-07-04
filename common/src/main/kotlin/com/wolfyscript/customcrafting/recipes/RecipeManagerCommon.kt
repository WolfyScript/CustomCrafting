package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.resource.LoadedRecipe
import com.wolfyscript.customcrafting.resource.ResourceListener
import com.wolfyscript.customcrafting.resource.ResourceLoader
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.customcrafting.util.exportResource
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.verification.VerificationResult
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import java.io.File

class RecipeManagerCommon(val customCrafting: CustomCraftingCommon) : RecipeManager, ResourceListener {

    val index: RecipeIndex = RecipeIndex()

    /**
     * Recipes can be loaded by other plugins. We keep track of which recipes CC registers, to not unload third-party recipes, for example, on a reload.
     */
    val recipesLoadedByCC: MutableSet<Key> = ObjectOpenHashSet()

    override val disabledRecipes: MutableSet<Key> = ObjectOpenHashSet()

    val awaitingDependenciesRecipes: MutableMap<Key, LoadedRecipe> = Object2ObjectOpenHashMap()
    val awaitingVerificationRecipes: MutableMap<Key, CustomRecipe<*, *>> = Object2ObjectOpenHashMap()
    val invalidRecipes: MutableList<VerificationResult<CustomRecipe<*, *>>> = mutableListOf()

    override fun onPrepare(resourceLoader: ResourceLoader) {
        exportDefaults(resourceLoader)
    }

    override fun onInitialLoad(resourceLoader: ResourceLoader) {
        resourceLoader.destinations.forEach { dest ->
            dest.load {
                customCrafting.logger.info("  loaded recipe: ${it.key} -> ${it.recipe}")
                awaitingDependenciesRecipes.put(it.key, it)
            }
        }

        // Check for dependencies
        val iterator = awaitingDependenciesRecipes.iterator()
        while (iterator.hasNext()) {
            val recipe = iterator.next()
            if (recipe.value.areDependenciesSatisfied()) {
                awaitingVerificationRecipes.put(recipe.key, recipe.value.recipe)
                iterator.remove()
            }
        }
    }

    override fun onReload(resourceLoader: ResourceLoader) {

    }

    override fun onFinalize(resourceLoader: ResourceLoader) {
        val recipeManager = customCrafting.recipeManager

        val previousLoaded = recipeManager.recipesLoadedByCC.toSet()
        recipeManager.recipesLoadedByCC.clear()

        for ((key, recipe) in awaitingVerificationRecipes) {
            // TODO: Verification
            recipeManager.updateRecipe(key, recipe)
        }

        // Remove recipes that are no longer loaded
        val removed = previousLoaded.subtract(recipeManager.recipesLoadedByCC)
        for (recipeKey in removed) {
            recipeManager.removeRecipe(recipeKey)
        }
    }

    private fun exportDefaults(resourceLoader: ResourceLoader) {
        customCrafting.logger.info("Exporting default recipes to ${resourceLoader}")
        val dir = "com/wolfyscript/customcrafting/recipes/default"
        listOf(
            "enchanted_golden_apple",
            "rotten_flesh_to_leather_smelting",
            "rotten_flesh_to_leather_smoking",
            "stick_to_torch_campfire",
            "stick_to_soul_torch_soul_campfire",
            "sus_stew_op",
            "upgrade_netherite_sword",
            "repair_with_amethyst"
        ).forEach {
            customCrafting.logger.info("  - recipe: $it")
            exportResource("$dir/$it.conf", File(resourceLoader.directory, "default/$it.conf"))
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
            disabledRecipes.add(recipe)
        }
    }

    override fun enableRecipe(key: Key) {
        disabledRecipes.remove(key)
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