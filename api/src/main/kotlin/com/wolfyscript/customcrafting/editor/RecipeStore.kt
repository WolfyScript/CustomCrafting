package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeConditions

/**
 * Stores the settings for a recipe in the editor.
 *
 * These settings are completed by the user in a GUI or otherwise and once complete used to construct a [CustomRecipe].
 * Before the recipe is constructed, the values are validated to make sure they create a valid recipe.
 */
interface RecipeStore {

    val priority: Int

    val condition: RecipeConditions?

    val recipeTypeSpecificStore: RecipeTypeSpecificStore

    fun complete() : CustomRecipe

    /**
     * Stores the settings for a specific type of recipe.
     */
    interface RecipeTypeSpecificStore

}