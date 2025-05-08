package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.registry.CCBuiltInRegistries
import com.wolfyscript.customcrafting.resource.DataManager

abstract class CustomCraftingCommon : CustomCrafting {

    override val recipeManager: RecipeManager
        get() = TODO("Not yet implemented")
    override val dataManager: DataManager
        get() = TODO("Not yet implemented")
    override val registries: CCBuiltInRegistries
        get() = TODO("Not yet implemented")

}