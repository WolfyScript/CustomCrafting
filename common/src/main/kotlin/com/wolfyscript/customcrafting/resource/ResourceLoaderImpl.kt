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

    override val destinations: List<ResourceLoader.Destination> = settings.destinations.map {
        customCrafting.logger.info("Construct destination: $it")
        return@map it.configureDestination(customCrafting, this)
    }

    override fun registerListener(listener: ResourceListener) {
        listeners.add(listener)
    }

    override fun loadResources() {
        if (!directory.exists()) {
            directory.mkdirs()
        }
        for (listener in listeners) {
            listener.onPrepare(this)
        }

        for (listener in listeners) {
            listener.onInitialLoad(this)
        }

        for (listener in listeners) {
            listener.onFinalize(this)
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

    data class LoadedRecipeImpl(override val key: Key, override val recipe: CustomRecipe<*, *>, override val dependencies: List<Dependency>) : LoadedRecipe {

        override fun areDependenciesSatisfied(): Boolean {
            return dependencies.all { it.isAvailable }
        }

    }

}
