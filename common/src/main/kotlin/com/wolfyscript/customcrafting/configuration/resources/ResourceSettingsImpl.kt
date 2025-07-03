package com.wolfyscript.customcrafting.configuration.resources

import com.fasterxml.jackson.annotation.JsonCreator

class ResourceSettingsImpl @JsonCreator constructor(
    override val destinations: List<DestinationSettings>,
) : ResourceSettings {

    override fun toString(): String {
        return "ResourceSettingsImpl(destinations=$destinations)"
    }
}