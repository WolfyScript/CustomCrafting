package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.factories.Factories
import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistries
import com.wolfyscript.customcrafting.resource.ResourceManager
import org.slf4j.Logger

interface CustomCrafting {

    companion object {
        const val PATH_TO_INTERNAL_BOOTSTRAP: String = "com.wolfyscript.customcrafting.InternalBootstrap"
    }

    val recipeManager: RecipeManager

    val resourceManager: ResourceManager

    val registries: CustomCraftingRegistries

    val configurationManager: ConfigurationManager

    val factories: Factories

    val logger: Logger

}