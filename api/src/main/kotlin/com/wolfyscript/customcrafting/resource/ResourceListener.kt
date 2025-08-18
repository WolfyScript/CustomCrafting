package com.wolfyscript.customcrafting.resource

/**
 * Listens to the events of a [ResourceLoader].
 *
 */
interface ResourceListener {

    /**
     * Called before the resources are loaded.
     * Used to, for example, export defaults.
     */
    fun onPrepare(resourceLoader: ResourceLoader) {}

    /**
     * Called on the initial loading of resources on startup
     */
    fun onInitialLoad(resourceLoader: ResourceLoader)

    fun onReload(resourceLoader: ResourceLoader)

    /**
     * Called after the resources were all loaded.
     */
    fun onFinalize(resourceLoader: ResourceLoader) {}

}