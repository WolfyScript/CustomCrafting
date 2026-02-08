package com.wolfyscript.customcrafting.editor.ext

import com.wolfyscript.customcrafting.core.recipes.CustomRecipe

interface EditorModelFactory<M> {

    val modelType: Class<M>

    fun loadIntoModel(recipe: CustomRecipe<*, *>): M

    fun createEmptyModel(): M

}