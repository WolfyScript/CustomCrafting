package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.scafall.identifier.Key

/**
 * References a recipe in the RecipeIndex.
 *
 * Recipes may be removed/updated at random, so care should be taken when accessing the [value].
 * If this reference refers to a recipe that was removed, then [value] will be null!
 *
 * **The [value] must not be cached to avoid memory leaks!**
 *
 * It should always be checked if the recipe is available before accessing it (even if you registered a recipe yourself, other plugins may remove it).
 */
interface RecipeReference<out T: CustomRecipe<*,*>> {

    /**
     * The key of the recipe in the RecipeIndex
     */
    val key: Key

    /**
     * The type of the recipe in the RecipeIndex
     */
    val type: RecipeType<*>

    /**
     * The actual recipe instance in the RecipeIndex.
     *
     * May be null, when the recipe was removed since this reference was fetched from the RecipeIndex.
     */
    val value: T?

    companion object {

        fun <T: CustomRecipe<*,*>> of(key: Key, recipe: T): RecipeReference<T> {
            return RecipeReferenceImpl(key, recipe)
        }

    }

}