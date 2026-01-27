package com.wolfyscript.customcrafting.resource

import com.wolfyscript.scafall.identifier.Key
import java.io.File

/**
 * Loads and Saves resources from/to specified destinations.
 * The destinations can be local or remote.
 */
interface ResourceLoader {

    val directory: File

    val sources: List<Source>

    /**
     * Registers a listener that can listen to the loading process to process custom resources, and reload resources when CustomCrafting reloads.
     */
    fun registerListener(listener: ResourceListener)

    /**
     * Loads resources from the specified destinations.
     */
    fun loadResources()

    /**
     * Stores the recipe to the destinations.
     * To which destination the recipe is stored depends on the configuration.
     */
    fun <T: Any> save(type: DataType<T>, key: Key, value: T)

    /**
     * Deletes the recipe from every destination.
     */
    fun delete(type: DataType<Any>, key: Key)

}