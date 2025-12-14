package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.editor.conditions.RecipeConditionsStore
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeType

/**
 * Stores the settings for a recipe in the editor.
 *
 * These settings are completed by the user in a GUI or otherwise and once complete used to construct a [CustomRecipe].
 * Before the recipe is constructed, the values are validated to make sure they create a valid recipe.
 */
interface RecipeStore<T: CustomRecipe<*,*>> {

    val recipeType: RecipeType<T>

    val recipeTypeSpecificStore: RecipeTypeSpecificStore<T>

    var priority: Int

    var condition: RecipeConditionsStore?

    /**
     * Completes the settings for the recipe.
     *
     * @return The completed recipe; or an error if there was a problem completing the recipe.
     */
    fun complete() : Result<T>

    /**
     * Stores the settings for a specific type of recipe.
     */
    interface RecipeTypeSpecificStore<T: CustomRecipe<*,*>> {

        /**
         * Completes the type specific properties
         *
         * @return the new [T] instance or an error otherwise
         */
        fun complete(common: RecipeStore<T>) : Result<T>

        /**
         * Used to construct [RecipeTypeSpecificStore]s instances,
         * either new instances or by loading existing [CustomRecipe]s properties into a [RecipeTypeSpecificStore].
         */
        interface Factory<T: CustomRecipe<*,*>> {

            val recipeType: RecipeType<T>

            /**
             * Loads the recipe into a store to be edited.
             *
             * Care needs to be taken, so that objects are completely cloned, so no references to the original properties exist!
             */
            fun edit(recipe: T): RecipeTypeSpecificStore<T>

            /**
             * Creates a new instance of a [RecipeTypeSpecificStore] for not yet existing recipe.
             */
            fun create(): RecipeTypeSpecificStore<T>

        }

    }

}