package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.editor.domain.model.recipe.conditions.RecipeConditionsModel
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeType

/**
 * Reflects the settings for a recipe in the editor.
 *
 * These settings are completed by the user in a GUI or otherwise and once complete used to construct a [CustomRecipe].
 * Before the recipe is constructed, the values are validated to make sure they create a valid recipe.
 */
interface RecipeModel<T: CustomRecipe<*, *>> {

    val recipeType: RecipeType<T>

    val recipeTypeSpecificModel: RecipeTypeSpecificModel<T>

    var priority: Int

    var condition: RecipeConditionsModel?

    /**
     * Completes the settings for the recipe.
     *
     * @return The completed recipe; or an error if there was a problem completing the recipe.
     */
    fun complete() : Result<T>

    /**
     * Stores the settings for a specific type of recipe.
     */
    interface RecipeTypeSpecificModel<T: CustomRecipe<*, *>> {

        /**
         * Completes the type specific properties
         *
         * @return the new [T] instance or an error otherwise
         */
        fun complete(common: RecipeModel<T>) : Result<T>

        /**
         * Used to construct [RecipeTypeSpecificModel]s instances,
         * either new instances or by loading existing [CustomRecipe]s properties into a [RecipeTypeSpecificModel].
         */
        interface Factory<T: CustomRecipe<*, *>> {

            val recipeType: RecipeType<T>

            /**
             * Loads the recipe into a store to be edited.
             *
             * Care needs to be taken, so that objects are completely cloned, so no references to the original properties exist!
             */
            fun edit(recipe: T): RecipeTypeSpecificModel<T>

            /**
             * Creates a new instance of a [RecipeTypeSpecificModel] for not yet existing recipe.
             */
            fun create(): RecipeTypeSpecificModel<T>

        }

    }

}