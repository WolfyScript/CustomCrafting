package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.recipes.RecipeManagerCommon
import com.wolfyscript.customcrafting.registry.CCBuiltInRegistries
import com.wolfyscript.customcrafting.registry.CCBuiltInRegistriesCommon
import com.wolfyscript.customcrafting.resource.DataManager
import com.wolfyscript.customcrafting.resource.DataManagerCommon

abstract class CustomCraftingCommon : CustomCrafting {

    override val configurationManager: ConfigurationManager = ConfigurationManagerImpl()
    override val recipeManager: RecipeManager = RecipeManagerCommon(this)
    override val dataManager: DataManager = DataManagerCommon(this)
    override val registries: CCBuiltInRegistries = CCBuiltInRegistriesCommon()

    init {



    }
}