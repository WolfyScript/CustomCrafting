package com.wolfyscript.customcrafting.configuration.resources

import com.fasterxml.jackson.annotation.JsonCreator

class ResourceSettingsImpl @JsonCreator constructor(
    override val sources: List<SourceSettings>,
    override val backup: BackupSettings = BackupSettingsImpl(emptyList()),
) : ResourceSettings {

    override fun toString(): String {
        return "ResourceSettings (sources=$sources)"
    }
}