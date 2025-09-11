package com.wolfyscript.customcrafting.configuration.resources

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.resource.Source
import com.wolfyscript.customcrafting.resource.DirectorySource
import com.wolfyscript.customcrafting.resource.ResourceLoader

class DirectorySourceSettingsImpl(
    override val path: String?,
    override val filter: SourceSettings.FilterSettings? = null,
    override val overwriteExisting: Boolean,
    override val propagateSavedResources: Boolean,
) : SourceSettings.DirectorySourceSettings {

    override fun toString(): String {
        return "Local $path (filter=$filter, overwrite=$overwriteExisting, propagateSaved=$propagateSavedResources)"
    }

    override fun configureFor(
        customCrafting: CustomCrafting,
        resourceLoader: ResourceLoader,
    ): Source {
        return DirectorySource(customCrafting, resourceLoader, this)
    }

}

