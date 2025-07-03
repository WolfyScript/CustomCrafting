package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.configuration.resources.ResourceSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.scafall.dependency.Dependency
import com.wolfyscript.scafall.identifier.Key
import java.io.File

class ResourceLoaderImpl(
    val customCrafting: CustomCraftingCommon,
    val settings: ResourceSettings,
    override val directory: File,
) :
    ResourceLoader {

    val listeners: MutableList<ResourceListener> = mutableListOf()

    override val destinations: List<AbstractDestination<*>> = settings.destinations.mapNotNull {
        customCrafting.logger.info("Construct destination: $it")
        return@mapNotNull when (it) {
            // TODO: hmmm
            is DestinationSettings.DirectoryDestinationSettings -> DirectoryDestination(customCrafting, this, it)
            is DestinationSettings.SQLDestinationSettings -> SQLDestination(customCrafting, this, it)
            else -> null
        }
    }


    override fun loadResources() {
        if (!directory.exists()) {
            directory.mkdirs()
        }
        // TODO:
        // 1. load recipes into separate storage
        // 2. verify recipes
        // 3. register new recipes
        // 3. replace existing recipes
        // 4. remove existing recipes that are no longer in storage
        for (listener in listeners) {
            listener.onInitialLoad(this)
        }

    }

    /**
     * Verifies that the loaded resources are configured correctly.
     * For example, no invalid values, dependencies are available, etc.
     */
    fun verifyResources() {

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

    data class LoadedRecipeImpl(override val key: Key, override val recipe: CustomRecipe<*, *>, override val dependencies: List<Dependency>) : LoadedRecipe {

        override fun areDependenciesSatisfied(): Boolean {
            return dependencies.all { it.isAvailable }
        }

    }

}
