package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.recipes.RecipeManagerCommon
import com.wolfyscript.customcrafting.registry.CCBuiltInRegistries
import com.wolfyscript.customcrafting.resource.DataManager
import com.wolfyscript.customcrafting.resource.DataManagerCommon

abstract class CustomCraftingCommon : CustomCrafting {

    override val recipeManager: RecipeManager = RecipeManagerCommon()
    override val dataManager: DataManager = DataManagerCommon()
    override val registries: CCBuiltInRegistries
        get() = TODO("Not yet implemented")

}