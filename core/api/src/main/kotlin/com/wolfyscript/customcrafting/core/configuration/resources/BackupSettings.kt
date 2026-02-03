package com.wolfyscript.customcrafting.core.configuration.resources

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.resource.BackupDestination
import com.wolfyscript.customcrafting.core.resource.ResourceLoader

interface BackupSettings {

    /**
     * A list of destinations to save backups to.
     */
    val destinations: List<BackupDestinationSettings>

    /**
     * A destination to save backups to.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonSubTypes(
        JsonSubTypes.Type(value = DirectoryBackupDestinationSettings::class, name = "directory")
    )
    @JsonPropertyOrder(value = ["type"])
    interface BackupDestinationSettings {

        /**
         * How many past backups to keep.
         */
        val keep: Int

        fun configureFor(customCrafting: com.wolfyscript.customcrafting.core.CustomCrafting, resourceLoader: com.wolfyscript.customcrafting.core.resource.ResourceLoader, backupSettings: BackupSettings): com.wolfyscript.customcrafting.core.resource.BackupDestination

    }

    /**
     * Creates a new directory/zip (see [compress]) for each new backup within the specified [path].
     */
    interface DirectoryBackupDestinationSettings : BackupDestinationSettings {

        /**
         * The path to the directory in which to create backups.
         * Either absolute or relative to the resource directory.
         */
        val path: String

        /**
         * Whether to compress the created directories as zip files.
         */
        val compress: Boolean

    }

}