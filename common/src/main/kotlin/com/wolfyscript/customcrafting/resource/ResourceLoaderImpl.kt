package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.configuration.resources.ResourceSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.scafall.identifier.Key
import java.io.File

class ResourceLoaderImpl(
    val customCrafting: CustomCraftingCommon,
    val settings: ResourceSettings,
    override val directory: File,
) :
    ResourceLoader {

    val listeners: MutableList<ResourceListener> = mutableListOf()

    override val sources: List<Source> = settings.sources.map {
        customCrafting.logger.info("[Resources] Construct sources: $it")
        return@map it.configureFor(customCrafting, this)
    }

    override fun registerListener(listener: ResourceListener) {
        listeners.add(listener)
    }

    override fun loadResources() {
        if (!directory.exists()) {
            directory.mkdirs()
        }
        customCrafting.logger.info("[Resources] $directory: Initiate resource loading with listeners: $listeners")
        customCrafting.logger.info("[Resources] $directory: Preparing resources...")
        for (listener in listeners) {
            listener.onPrepare(this)
        }

        customCrafting.logger.info("[Resources] $directory: Loading resources...")
        for (listener in listeners) {
            listener.onInitialLoad(this)
        }

        customCrafting.logger.info("[Resources] $directory: Finalize resources...")
        for (listener in listeners) {
            listener.onFinalize(this)
        }
    }

    override fun save(key: Key, recipe: CustomRecipe<*, *>) {
        for (destination in sources) {
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
        for (destination in sources) {
            if (!(destination.filter?.accepts(key, recipe) ?: true)) {
                continue
            }
            val result = destination.delete(key, recipe)
            if (result.isSuccess && result.getOrNull() == true) {
                // TODO: Propagate deletion?
            }
        }
    }

    data class LoadedRecipeImpl(override val key: Key, override val recipe: CustomRecipe<*, *>) : LoadedRecipe

}
