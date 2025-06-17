package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.factories.Factories
import com.wolfyscript.customcrafting.factories.FactoriesCommon
import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.recipes.RecipeManagerCommon
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistriesCommon

abstract class CustomCraftingCommon : CustomCrafting {

    // Order of initiation is important, almost everything uses Factories, then the second most used are the Registries
    override val factories: Factories = FactoriesCommon()
    override val registries = CustomCraftingRegistriesCommon()

    init {
        registries.initRegistries()
    }

    // Then continue with the other setup
    override val recipeManager: RecipeManager = RecipeManagerCommon(this)

    abstract fun load()

    abstract fun enabled()

    abstract fun unload()

}