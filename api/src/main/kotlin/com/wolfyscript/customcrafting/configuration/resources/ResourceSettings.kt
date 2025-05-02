package com.wolfyscript.customcrafting.configuration.resources

interface ResourceSettings {

    /**
     * A list of destinations to save resources to and load resources from.
     */
    val destinations: List<DestinationSettings>

}

/**
 * Settings for a destination to save resources to and load resources from.
 */
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
