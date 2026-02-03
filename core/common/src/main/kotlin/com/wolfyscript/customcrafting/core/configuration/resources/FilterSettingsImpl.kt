package com.wolfyscript.customcrafting.core.configuration.resources

import com.wolfyscript.customcrafting.configuration.resources.SourceSettings

class FilterSettingsImpl(
    override val includes: SourceSettings.FilterSettings.FilterEntry? = null,
    override val excludes: SourceSettings.FilterSettings.FilterEntry? = null,
) : SourceSettings.FilterSettings {

    override fun toString(): String {
        return "Filter (includes=$includes, excludes=$excludes)"
    }
}

class FilterEntryImpl(
    override val namespaces: List<String> = emptyList(),
    override val paths: List<String> = emptyList(),
    override val regex: List<String> = emptyList(),
) : SourceSettings.FilterSettings.FilterEntry {

    override fun toString(): String {
        return "(namespaces=$namespaces, paths=$paths, regex=$regex)"
    }
}
