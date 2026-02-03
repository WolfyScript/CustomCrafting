package com.wolfyscript.customcrafting.core.configuration.resources

import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.configuration.resources.SourceSettings
import com.wolfyscript.customcrafting.core.resource.Source
import com.wolfyscript.customcrafting.core.resource.ResourceLoader
import com.wolfyscript.customcrafting.core.resource.database.SQLSource

class SQLSourceSettingsImpl(
    override val connection: SourceSettings.SQLSourceSettings.DatabaseConnectionType,
    override val filter: SourceSettings.FilterSettings? = null,
    override val overwriteExisting: Boolean,
    override val propagateSavedResources: Boolean,
) : SourceSettings.SQLSourceSettings {

    override fun toString(): String {
        return "SQL connecting to $connection, filter=$filter, overwriteExisting=$overwriteExisting, propagateSaved=$propagateSavedResources)"
    }

    override fun configureFor(
        customCrafting: CustomCrafting,
        resourceLoader: ResourceLoader,
    ): Source {
        return SQLSource(customCrafting, this)
    }
}
