package com.wolfyscript.customcrafting.factories

import com.wolfyscript.customcrafting.CustomCraftingProvider

interface Factories {

    companion object : Factories by CustomCraftingProvider.get().factories

    val recipeFactory: RecipeFactory

}