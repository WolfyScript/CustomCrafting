package com.wolfyscript.customcrafting.editor

import com.wolfyscript.scafall.identifier.Key

interface SessionState {

    val recipeStore: RecipeStore<*>

    fun save()

    fun cancel()

    interface EditState : SessionState {

        val currentKey: Key

        fun saveAs(key: Key)

    }

    interface CreateState : SessionState

}