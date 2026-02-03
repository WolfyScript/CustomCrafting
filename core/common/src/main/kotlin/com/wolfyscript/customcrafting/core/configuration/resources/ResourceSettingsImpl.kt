package com.wolfyscript.customcrafting.core.configuration.resources

import com.fasterxml.jackson.annotation.JsonCreator
import com.wolfyscript.customcrafting.core.configuration.resources.BackupSettings
import com.wolfyscript.customcrafting.core.configuration.resources.ResourceSettings
import com.wolfyscript.customcrafting.core.configuration.resources.SourceSettings

class ResourceSettingsImpl @JsonCreator constructor(
    override val sources: List<SourceSettings>,
    override val backup: BackupSettings = BackupSettingsImpl(emptyList()),
) : ResourceSettings {

    override fun toString(): String {
        return "ResourceSettings (sources=$sources)"
    }
}