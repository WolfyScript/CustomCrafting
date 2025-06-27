package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.configuration.resources.ResourceSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.util.exportResource
import com.wolfyscript.scafall.dependency.Dependency
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.verification.VerificationResult
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.io.File

class ResourceLoaderImpl(val customCrafting: CustomCraftingCommon, val settings: ResourceSettings, val directory: File) :
    ResourceLoader {

    override val destinations: List<AbstractDestination<*>> = settings.destinations.mapNotNull {
        customCrafting.logger.info("Construct destination: $it")
        return@mapNotNull when (it) {
            // TODO: hmmm
            is DestinationSettings.DirectoryDestinationSettings -> DirectoryDestination(customCrafting, this, it)
            is DestinationSettings.SQLDestinationSettings -> SQLDestination(customCrafting, this, it)
            else -> null
        }
    }

    val awaitingDependenciesRecipes: MutableMap<Key, LoadedRecipe> = Object2ObjectOpenHashMap()
    val awaitingVerificationRecipes: MutableMap<Key, CustomRecipe<*, *>> = Object2ObjectOpenHashMap()
    val invalidRecipes: MutableList<VerificationResult<CustomRecipe<*, *>>> = mutableListOf()

    private fun exportDefaults() {
        customCrafting.logger.info("Exporting default recipes to $directory")
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
            exportResource("$dir/$it.conf", File(directory, "default/$it.conf"))
        }
    }

    override fun loadResources() {
        if (!directory.exists()) {
            directory.mkdirs()
        }
        exportDefaults()
        // TODO:
        // 1. load recipes into separate storage
        // 2. verify recipes
        // 3. register new recipes
        // 3. replace existing recipes
        // 4. remove existing recipes that are no longer in storage

        loadIntoTemporaryStorage()

        // Check for dependencies
        val iterator = awaitingDependenciesRecipes.iterator()
        while (iterator.hasNext()) {
            val recipe = iterator.next()
            if (recipe.value.areDependenciesSatisfied()) {
                awaitingVerificationRecipes.put(recipe.key, recipe.value.recipe)
                iterator.remove()
            }
        }

        registerLoadedRecipes()
    }

    fun loadIntoTemporaryStorage() {
        customCrafting.logger.info("Loading resources...")
        // Load resources into a temporary storage
        destinations.forEach { it.load() }
    }

    internal fun addRecipeFrom(recipe: LoadedRecipe, destination: AbstractDestination<*>) {
        if (!destination.settings.overwriteExisting && awaitingDependenciesRecipes.containsKey(recipe.key)) {
            return
        }
        awaitingDependenciesRecipes[recipe.key] = recipe
    }

    /**
     * Verifies that the loaded resources are configured correctly.
     * For example, no invalid values, dependencies are available, etc.
     */
    fun verifyResources() {

    }

    /**
     * Load recipes loaded in temporary storage into registry.
     */
    fun registerLoadedRecipes() {
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


    override fun save(key: Key, recipe: CustomRecipe<*, *>) {
        for (destination in destinations) {
            if (!(destination.filter?.accepts(key, recipe) ?: true)) {
                continue
            }
            val result = destination.save(key, recipe)
            if (result.isSuccess && result.getOrNull() == true) {
                if (!destination.settings.propagateSavedResources) {
                    break
                }
            }
        }
    }

    override fun delete(key: Key, recipe: CustomRecipe<*, *>) {
        for (destination in destinations) {
            if (!(destination.filter?.accepts(key, recipe) ?: true)) {
                continue
            }
            if (destination.settings.backup != null) {
                continue
            }
            val result = destination.delete(key, recipe)
            if (result.isSuccess && result.getOrNull() == true) {
                // TODO: Propagate deletion?
            }
        }
    }

    override fun createBackup() {

    }

    data class LoadedRecipe(val key: Key, val recipe: CustomRecipe<*, *>, val dependencies: List<Dependency>) {

        fun areDependenciesSatisfied(): Boolean {
            return dependencies.all { it.isAvailable }
        }

    }

}
