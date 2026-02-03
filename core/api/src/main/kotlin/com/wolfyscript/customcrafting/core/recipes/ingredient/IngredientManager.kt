package com.wolfyscript.customcrafting.core.recipes.ingredient

import com.wolfyscript.customcrafting.core.resource.ResourceListener
import com.wolfyscript.scafall.identifier.Key

/**
 * Manages ingredients that are saved and which can be reused.
 */
interface IngredientManager : com.wolfyscript.customcrafting.core.resource.ResourceListener {

    companion object {
        const val LOG_PREFIX = "[Ingredient Manager] "
    }

    fun registerIngredient(key: Key, ingredient: Ingredient)

    fun getIngredient(key: Key): Ingredient?

    fun getKey(ingredient: Ingredient): Key?

}