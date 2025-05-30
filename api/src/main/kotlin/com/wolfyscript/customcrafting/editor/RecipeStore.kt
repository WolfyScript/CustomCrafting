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

        fun complete() : Result<T>

    }

}