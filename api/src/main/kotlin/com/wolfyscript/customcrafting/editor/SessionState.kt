package com.wolfyscript.customcrafting.editor

import com.wolfyscript.scafall.identifier.Key

interface SessionState {

    val recipeState: RecipeState<*>

    fun cancel()

    /**
     * The Recipe Editor editing an existing recipes
     */
    interface EditState : SessionState {

        /**
         * The key of the existing recipe
         */
        val currentKey: Key

        /**
         * Saves the edited recipe, overwriting the existing recipe.
         */
        fun save()

        /**
         * Saves the edited recipe with a new key.
         * The existing recipe is not overwritten/deleted!
         */
        fun saveAs(key: Key)

    }

    /**
     * The Recipe Editor creating a new recipe
     */
    interface CreateState : SessionState {

        /**
         * Saves a new recipe under the specified key
         */
        fun save(key: Key)

    }

}