package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.factories.Factories
import com.wolfyscript.customcrafting.factories.FactoriesCommon
import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.recipes.RecipeManagerCommon
import com.wolfyscript.customcrafting.registry.CCBuiltInRegistries
import com.wolfyscript.customcrafting.registry.CCBuiltInRegistriesCommon
import com.wolfyscript.customcrafting.resource.DataManager
import com.wolfyscript.customcrafting.resource.DataManagerCommon
import kotlin.io.path.Path

abstract class CustomCraftingCommon : CustomCrafting {

    override val configurationManager: ConfigurationManager = ConfigurationManagerImpl(Path("config"))
    override val recipeManager: RecipeManager = RecipeManagerCommon(this)
    override val registries: CCBuiltInRegistries = CCBuiltInRegistriesCommon()
    override val factories: Factories = FactoriesCommon()

    abstract fun load()

    abstract fun unload()

}