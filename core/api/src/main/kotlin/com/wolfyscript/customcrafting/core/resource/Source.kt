package com.wolfyscript.customcrafting.core.resource

import com.wolfyscript.customcrafting.core.configuration.resources.SourceSettings
import com.wolfyscript.scafall.identifier.Key

/**
 * The destination to load or save resources from/to.
 *
 * See [DestinationSettings][com.wolfyscript.customcrafting.core.configuration.resources.SourceSettings] for configuration.
 */
interface Source {

    val filter: Filter?

    val settings: SourceSettings

    fun <T: Any> load(type: DataType<T>, accept: (value: LoadedObject<T>) -> Unit)

    /**
     * Tries to save the value to this destination.
     *
     * @return A Result of whether the value was stored; or an exception when an error occurred.
     */
    fun <T: Any> save(type: DataType<T>, key: Key, value: T): Result<Boolean>

    /**
     * Tries to delete the value from this destination.
     *
     * @return A Result of whether the value was deleted; or an exception when an error occurred.
     */
    fun delete(type: DataType<Any>, key: Key): Result<Boolean>

    /**
     * Filters out resource keys that should not be loaded or saved.
     */
    interface Filter {

        /**
         * Checks if the key should be accepted.
         *
         * @return True if the key should be accepted; false otherwise.
         */
        fun accepts(key: Key) : Boolean

    }

}

