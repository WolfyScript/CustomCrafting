package com.wolfyscript.customcrafting.configuration.resources

class LocalDestinationSettingsImpl(
    override val path: String?,
    override val filter: DestinationSettings.FilterSettings? = null,
    override val overwriteExisting: Boolean,
    override val propagateSavedResources: Boolean,
    override val backup: DestinationSettings.BackupSettings? = null,
) : DestinationSettings.LocalDestinationSettings {

    override fun toString(): String {
        return "LocalDestinationSettingsImpl(path=$path, filter=$filter, overwriteExisting=$overwriteExisting, propagateSavedResources=$propagateSavedResources, backup=$backup)"
    }
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
    override val backup: DestinationSettings.BackupSettings? = null,
) : DestinationSettings.SQLDestinationSettings {

    override fun toString(): String {
        return "SQLDestinationSettingsImpl(host='$host', port=$port, schema='$schema', username='$username', password='$password', filter=$filter, overwriteExisting=$overwriteExisting, propagateSavedResources=$propagateSavedResources, backup=$backup)"
    }
}

class BackupSettingsImpl() : DestinationSettings.BackupSettings

class FilterSettingsImpl(
    override val includes: DestinationSettings.FilterSettings.FilterEntry? = null,
    override val excludes: DestinationSettings.FilterSettings.FilterEntry? = null,
) : DestinationSettings.FilterSettings {

    override fun toString(): String {
        return "FilterSettingsImpl(includes=$includes, excludes=$excludes)"
    }
}

class FilterEntryImpl(
    override val namespaces: List<String> = emptyList(),
    override val paths: List<String> = emptyList(),
    override val regex: List<String> = emptyList(),
) : DestinationSettings.FilterSettings.FilterEntry {

    override fun toString(): String {
        return "FilterEntryImpl(namespaces=$namespaces, paths=$paths, regex=$regex)"
    }
}
