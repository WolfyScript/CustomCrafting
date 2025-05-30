package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.recipes.CustomRecipe

/**
 * Loads and Saves resources from/to specified destinations.
 * The destinations can be local or remote.
 */
interface ResourceLoader {

    val destinations: List<Destination>

    /**
     * Loads resources from the specified destinations.
     */
    fun loadResources()

    /**
     * Verifies that the loaded resources are configured correctly.
     * For example, no invalid values, dependencies are available, etc.
     */
    fun verifyResources()

    /**
     * Stores the recipe to the destinations.
     * To which destination the recipe is stored depends on the configuration.
     */
    fun save(recipe: CustomRecipe<*,*>)

    /**
     * Deletes the recipe from every destination.
     */
    fun delete(recipe: CustomRecipe<*,*>)

    /**
     * Creates a backup and stores it to backup-destinations (if available)
     */
    fun createBackup()


    /**
     * The destination to load or save resources from/to.
     *
     * See [DestinationSettings][com.wolfyscript.customcrafting.configuration.resources.DestinationSettings] for configuration.
     */
    interface Destination {

        val filter: Filter?

        fun load()

        /**
         * Tries to save the recipe to this destination.
         *
         * @return A Result of whether the recipe was stored; or an exception when an error occurred.
         */
        fun save(recipe: CustomRecipe<*,*>): Result<Boolean>

        /**
         * Tries to delete the recipe from this destination.
         *
         * @return A Result of whether the recipe was deleted; or an exception when an error occurred.
         */
        fun delete(recipe: CustomRecipe<*,*>): Result<Boolean>

        interface Filter {

            fun accepts(recipe: CustomRecipe<*,*>) : Boolean

        }

    }
}