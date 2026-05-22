package com.wolfyscript.customcrafting.core.resource

import com.wolfyscript.customcrafting.core.recipe.CustomRecipe
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient

interface DataType<T> {

    val id: String

    val classType: Class<T>

    object Recipes : DataType<CustomRecipe<*, *>> {
        override val id: String = "recipes"
        override val classType: Class<CustomRecipe<*, *>> = CustomRecipe::class.java
    }

    object Ingredients : DataType<Ingredient> {
        override val id: String = "ingredients"
        override val classType: Class<Ingredient> = Ingredient::class.java
    }

}