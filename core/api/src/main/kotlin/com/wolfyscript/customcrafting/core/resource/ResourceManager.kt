package com.wolfyscript.customcrafting.core.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.wolfyscript.customcrafting.core.CustomCrafting
import java.io.File

/**
 * Manages everything related to loading and accessing resources.
 * Resources are usually stored in the mod configuration directory within the `resources` subdirectory.
 */
interface ResourceManager {

    companion object {

        fun createNewForDir(customCrafting: CustomCrafting, directory: File): ResourceManager {
            return ResourceManagerCommon(customCrafting, directory)
        }

    }

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