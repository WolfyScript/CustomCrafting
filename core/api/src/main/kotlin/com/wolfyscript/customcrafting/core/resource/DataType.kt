package com.wolfyscript.customcrafting.core.resource

import com.wolfyscript.customcrafting.core.recipes.CustomRecipe
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient

interface DataType<T> {

    val id: String

    val classType: Class<T>

    object Recipes : DataType<com.wolfyscript.customcrafting.core.recipes.CustomRecipe<*, *>> {
        override val id: String = "recipes"
        override val classType: Class<com.wolfyscript.customcrafting.core.recipes.CustomRecipe<*, *>> = _root_ide_package_.com.wolfyscript.customcrafting.core.recipes.CustomRecipe::class.java
    }

    object Ingredients : DataType<com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient> {
        override val id: String = "ingredients"
        override val classType: Class<com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient> = _root_ide_package_.com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient::class.java
    }

}