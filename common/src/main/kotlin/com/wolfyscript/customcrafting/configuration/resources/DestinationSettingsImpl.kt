package com.wolfyscript.customcrafting.configuration.resources

class LocalDestinationSettingsImpl(
    override val path: String?,
    override val filter: DestinationSettings.FilterSettings? = null,
    override val overwriteExisting: Boolean,
    override val propagateSavedResources: Boolean,
    override val backup: DestinationSettings.BackupSettings? = null
) : DestinationSettings.LocalDestinationSettings {
}

class SQLDestinationSettingsImpl(
    override val host: String,
    override val port: Int,
    override val schema: String,
    override val username: String,
    override val password: String,
    override val filter: DestinationSettings.FilterSettings? = null,
    override val overwriteExisting: Boolean,
    override val propagateSavedResources: Boolean,
    override val backup: DestinationSettings.BackupSettings? = null
) : DestinationSettings.SQLDestinationSettings

class BackupSettingsImpl() : DestinationSettings.BackupSettings

class FilterSettingsImpl(
    override val includeNamespaces: List<String>,
    override val excludeNamespaces: List<String>
) : DestinationSettings.FilterSettings
