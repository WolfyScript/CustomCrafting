package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe

class SQLDestination(customCrafting: CustomCrafting, resourceLoaderImpl: ResourceLoaderImpl, settings: DestinationSettings.SQLDestinationSettings) :
    AbstractDestination<DestinationSettings.SQLDestinationSettings>(customCrafting, resourceLoaderImpl, settings) {

    override val filter: ResourceLoader.Destination.Filter? =
        settings.filter?.let { DestinationFilter(customCrafting, resourceLoaderImpl, it) }

    override fun load() {
        TODO("Not yet implemented")
    }

    override fun save(recipe: CustomRecipe): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun delete(recipe: CustomRecipe): Result<Boolean> {
        TODO("Not yet implemented")
    }

}
