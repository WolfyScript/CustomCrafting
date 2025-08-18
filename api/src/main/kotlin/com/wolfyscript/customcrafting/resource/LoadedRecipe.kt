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

    /**
     * The dependencies of this recipe.
     * These may be present if the recipe uses ItemStacks from third-party plugins/mods, or Items from third-party Mods.
     *
     * The recipe may only be registered when all of these dependencies are initialized (see [DependencyManager][com.wolfyscript.scafall.compat.DependencyManager]).
     * A dependency in this list may already be initialized at the time of loading.
     */
    val dependencies: Set<Key>

}