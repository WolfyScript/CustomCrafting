package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.configuration.resources.ResourceSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.util.exportResource
import com.wolfyscript.scafall.dependency.Dependency
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.verification.VerificationResult
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.io.File

class ResourceLoaderImpl(val customCrafting: CustomCrafting, val settings: ResourceSettings, val directory: File) : ResourceLoader {

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
    val awaitingVerificationRecipes: MutableMap<Key, CustomRecipe<*,*>> = Object2ObjectOpenHashMap()
    val invalidRecipes: MutableList<VerificationResult<CustomRecipe<*,*>>> = mutableListOf()

    fun addRecipeFrom(recipe: LoadedRecipe, destination: AbstractDestination<*>) {
        if (!destination.settings.overwriteExisting && awaitingDependenciesRecipes.containsKey(recipe.key)) {
            return
        }
        awaitingDependenciesRecipes[recipe.key] = recipe
    }

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
            "upgrade_netherite_sword"
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

        customCrafting.logger.info("Loading resources... ${destinations}")
        // Load resources into a temporary storage
        destinations.forEach {
            it.load()
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

    override fun verifyResources() {
        for ((key, recipe) in awaitingVerificationRecipes) {
            // TODO: Verification
            CustomCraftingRegistryTypes.customRecipes.resolveOrThrow().register(key, recipe)
        }
    }

    override fun save(recipe: CustomRecipe<*,*>) {
        for (destination in destinations) {
            if (!(destination.filter?.accepts(recipe) ?: true)) {
                continue
            }
            val result = destination.save(recipe)
            if (result.isSuccess && result.getOrNull() == true) {
                if (!destination.settings.propagateSavedResources) {
                    break
                }
            }
        }
    }

    override fun delete(recipe: CustomRecipe<*,*>) {
        for (destination in destinations) {
            if (!(destination.filter?.accepts(recipe) ?: true)) {
                continue
            }
            if (destination.settings.backup != null) {
                continue
            }
            val result = destination.delete(recipe)
            if (result.isSuccess && result.getOrNull() == true) {
                // TODO: Propagate deletion?
            }
        }
    }

    override fun createBackup() {

    }

    data class LoadedRecipe(val key: Key, val recipe: CustomRecipe<*,*>, val dependencies: List<Dependency>) {

        fun areDependenciesSatisfied(): Boolean {
            return dependencies.all { it.isAvailable }
        }

    }

}
