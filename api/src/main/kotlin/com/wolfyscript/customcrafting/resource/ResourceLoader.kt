package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.scafall.identifier.Key
import java.io.File

/**
 * Loads and Saves resources from/to specified destinations.
 * The destinations can be local or remote.
 */
interface ResourceLoader {

    val directory: File

    val destinations: List<Destination>

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
    fun save(key: Key, recipe: CustomRecipe<*,*>)

    /**
     * Deletes the recipe from every destination.
     */
    fun delete(key: Key, recipe: CustomRecipe<*,*>)

    /**
     * Creates a backup and stores it to backup-destinations (if available)
     */
    fun createBackup()

}