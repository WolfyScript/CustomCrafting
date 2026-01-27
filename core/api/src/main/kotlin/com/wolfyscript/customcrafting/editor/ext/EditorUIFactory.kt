package com.wolfyscript.customcrafting.editor.ext

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.recipes.CustomRecipe

interface EditorUIFactory<M> {

    val modelType: Class<M>

    fun loadIntoModel(recipe: CustomRecipe<*, *>): M

    fun createEmptyModel(): M

    @Composable
    fun renderUI(model: M)

}