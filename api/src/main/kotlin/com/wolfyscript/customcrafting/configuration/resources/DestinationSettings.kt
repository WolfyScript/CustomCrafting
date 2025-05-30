package com.wolfyscript.customcrafting.configuration.resources

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Settings for a destination to save resources to and load resources from.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSubTypes(
    JsonSubTypes.Type(value = DestinationSettings.SQLDestinationSettings::class, name = "sql"),
    JsonSubTypes.Type(value = DestinationSettings.LocalDestinationSettings::class, name = "local")
)
@JsonPropertyOrder(value = ["type"])
interface DestinationSettings {

    /**
     * Optional filter to specify which resources to save to this destination.
     * If not specified, all resources will be saved to this destination.
     */
    val filter: FilterSettings?

    /**
     * Whether to overwrite existing resources by resources from this destination.
     */
    val overwriteExisting: Boolean

    /**
     * Whether resources saved to this destination should propagate to destinations of lower priority.
     */
    val propagateSavedResources: Boolean

    /**
     * Optional setting to use this destination as a backup destination.
     * Backups are done before updates and resource upgrades.
     *
     * **Backup destinations are not used to load resources!**
     * **They are only used to save resources! Filter settings still apply!**
     */
    val backup: BackupSettings?

    interface LocalDestinationSettings : DestinationSettings {

        /**
         * An optional path to the resource directory.
         */
        val path: String?

    }

    interface SQLDestinationSettings : DestinationSettings {

        val host: String
        val port: Int
        val schema: String
        val username: String
        val password: String

    }

    interface FilterSettings {

        /**
         * Namespaces that are saved to this destination.
         * Only resources with these namespaces will be saved to this destination.
         */
        val includeNamespaces: List<String>

        /**
         * Namespaces that are not saved to this destination.
         * Resources with these namespaces will not be saved to this destination.
         */
        val excludeNamespaces: List<String>

    }

    interface BackupSettings {

        // TODO

    }

}