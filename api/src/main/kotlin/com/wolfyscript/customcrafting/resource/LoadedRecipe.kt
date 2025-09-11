package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.scafall.identifier.Key

/**
 * Recipe information loaded by a [ResourceLoader].
 */
interface LoadedRecipe {

    /**
     * The key of the recipe, constructed from the path and filename.
     */
    val key: Key

    /**
     * The recipe instance that was loaded.
     */
    val recipe: CustomRecipe<*, *>

}