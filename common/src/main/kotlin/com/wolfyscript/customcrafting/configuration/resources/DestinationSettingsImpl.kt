package com.wolfyscript.customcrafting.configuration.resources

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.resource.DirectoryDestination
import com.wolfyscript.customcrafting.resource.ResourceLoader

class DirectoryDestinationSettingsImpl(
    override val path: String?,
    override val filter: DestinationSettings.FilterSettings? = null,
    override val overwriteExisting: Boolean,
    override val propagateSavedResources: Boolean,
    override val backup: DestinationSettings.BackupSettings? = null,
) : DestinationSettings.DirectoryDestinationSettings {

    override fun toString(): String {
        return "LocalDestinationSettingsImpl(path=$path, filter=$filter, overwriteExisting=$overwriteExisting, propagateSavedResources=$propagateSavedResources, backup=$backup)"
    }

    override fun configureDestination(
        customCrafting: CustomCrafting,
        resourceLoader: ResourceLoader,
    ): ResourceLoader.Destination {
        return DirectoryDestination(customCrafting, resourceLoader, this)
    }

}

class BackupSettingsImpl(override val compress: Boolean = false) : DestinationSettings.BackupSettings

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
