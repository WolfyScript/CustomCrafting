package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.recipes.CustomRecipe

interface ResourceLoader {

    val destination: Destination

    fun loadResources()

    fun verifyResources()

    interface Destination {

        fun save(recipe: CustomRecipe<*>)

        fun delete(recipe: CustomRecipe<*>)

    }
}