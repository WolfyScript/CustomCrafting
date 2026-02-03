package com.wolfyscript.customcrafting.core.resource

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Manages everything related to loading and accessing resources.
 * Resources are usually stored in the mod configuration directory within the `resources` subdirectory.
 */
interface ResourceManager {

    /**
     * The resource loader used to load resources.
     */
    val resourceLoader: ResourceLoader

    val backupManager: BackupManager

    /**
     * The Jackson object mapper used to deserialize JSON files.
     */
    val jacksonObjectMapper: ObjectMapper

    fun loadResources()

}