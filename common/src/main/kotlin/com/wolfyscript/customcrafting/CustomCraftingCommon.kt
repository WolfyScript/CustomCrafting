package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.core.commands.CCCommands
import com.wolfyscript.customcrafting.factories.Factories
import com.wolfyscript.customcrafting.factories.FactoriesCommon
import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.recipes.RecipeManagerCommon
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistriesCommon

abstract class CustomCraftingCommon : CustomCrafting {

    // Order of initiation is important, almost everything uses Factories, then the second most used are the Registries
    override val factories: Factories = FactoriesCommon()
    override val registries = CustomCraftingRegistriesCommon()
    val commands: CCCommands = CCCommands(this)

    init {
        registries.initRegistries()
    }

    // Then continue with the other setup
    override val recipeManager: RecipeManagerCommon = RecipeManagerCommon(this)

}