package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.factories.Factories
import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistries
import com.wolfyscript.customcrafting.resource.DataManager
import org.slf4j.Logger

interface CustomCrafting {

    val recipeManager: RecipeManager

    val dataManager: DataManager

    val registries: CustomCraftingRegistries

    val configurationManager: ConfigurationManager

    val factories: Factories

    val logger: Logger

}