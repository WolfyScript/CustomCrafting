package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.registry.CCBuiltInRegistries
import com.wolfyscript.customcrafting.resource.DataManager

interface CustomCrafting {

    val recipeManager: RecipeManager

    val dataManager: DataManager

    val registries: CCBuiltInRegistries

    val configurationManager: ConfigurationManager

}