package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings

abstract class AbstractDestination<T: DestinationSettings>(val customCrafting: CustomCrafting, val resourceLoaderImpl: ResourceLoaderImpl, override val settings: T) : Destination {

    override val filter: Destination.Filter? =
        settings.filter?.let { DestinationFilter(customCrafting, it) }

}