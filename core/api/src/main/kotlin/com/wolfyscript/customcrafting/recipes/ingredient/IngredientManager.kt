package com.wolfyscript.customcrafting.recipes.ingredient

import com.wolfyscript.customcrafting.resource.ResourceListener
import com.wolfyscript.scafall.identifier.Key

/**
 * Manages ingredients that are saved and which can be reused.
 */
interface IngredientManager : ResourceListener {

    companion object {
        const val LOG_PREFIX = "[Ingredient Manager] "
    }

    fun registerIngredient(key: Key, ingredient: Ingredient)

    fun getIngredient(key: Key): Ingredient?

    fun getKey(ingredient: Ingredient): Key?

}