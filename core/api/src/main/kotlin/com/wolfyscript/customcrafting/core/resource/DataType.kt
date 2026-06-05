package com.wolfyscript.customcrafting.core.resource

import com.wolfyscript.customcrafting.core.recipe.CustomRecipe
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient

/**
 * The type of resources that are available via the [ResourceLoader]
 */
interface DataType<T> {

    /**
     * The unique id of the data type.
     * It is used for the resource subdirectory and other identification.
     */
    val id: String

    /**
     * The actual type this data type represents.
     * Usually the abstract class or interface from which all specific types inherit or implement.
     */
    val classType: Class<T>

    /**
     * The [DataType] for recipe resources
     */
    object Recipes : DataType<CustomRecipe<*, *>> {
        override val id: String = "recipes"
        override val classType: Class<CustomRecipe<*, *>> = CustomRecipe::class.java
    }

    /**
     * The [DataType] for ingredient resources.
     */
    object Ingredients : DataType<Ingredient> {
        override val id: String = "ingredients"
        override val classType: Class<Ingredient> = Ingredient::class.java
    }

}