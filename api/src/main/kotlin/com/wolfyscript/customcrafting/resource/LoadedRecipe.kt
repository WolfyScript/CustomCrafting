package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.scafall.identifier.Key

interface LoadedRecipe {

    val key: Key

    val recipe: CustomRecipe<*, *>

    val dependencies: Set<Key>

}