package com.wolfyscript.customcrafting.registry

import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.scafall.registry.TypeRegistry

interface CCBuiltInRegistries {

    val recipeTypes: TypeRegistry<RecipeType<*>>

}