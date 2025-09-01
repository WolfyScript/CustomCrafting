package com.wolfyscript.customcrafting.configuration.resources

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.resource.Destination
import com.wolfyscript.customcrafting.resource.ResourceLoader
import com.wolfyscript.customcrafting.resource.database.SQLDestination

class SQLDestinationSettingsImpl(
    override val connection: DestinationSettings.SQLDestinationSettings.DatabaseConnectionType,
    override val filter: DestinationSettings.FilterSettings? = null,
    override val overwriteExisting: Boolean,
    override val propagateSavedResources: Boolean,
    override val backup: DestinationSettings.BackupSettings? = null,
) : DestinationSettings.SQLDestinationSettings {

    override fun toString(): String {
        return "SQL connecting to $connection, filter=$filter, overwriteExisting=$overwriteExisting, propagateSavedResources=$propagateSavedResources, backup=$backup)"
    }

    override fun configureDestination(
        customCrafting: CustomCrafting,
        resourceLoader: ResourceLoader,
    ): Destination {
        return SQLDestination(customCrafting, resourceLoader, this)
    }
}
