package com.wolfyscript.customcrafting.core.resource

import com.wolfyscript.scafall.identifier.Key

/**
 * Object information loaded by a [ResourceLoader].
 */
interface LoadedObject<T> {

    /**
     * The key of the value, constructed from the path and filename.
     */
    val key: Key

    /**
     * The value instance that was loaded.
     */
    val value: T

}