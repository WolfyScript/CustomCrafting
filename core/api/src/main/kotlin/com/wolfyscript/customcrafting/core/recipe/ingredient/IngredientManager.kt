package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.recipe.IngredientManagerCommon
import com.wolfyscript.customcrafting.core.resource.ResourceListener
import com.wolfyscript.scafall.identifier.Key

/**
 * Manages ingredients that are saved and which can be reused.
 */
interface IngredientManager : ResourceListener {

    companion object {
        const val LOG_PREFIX = "[Ingredient Manager] "

        fun createNew(customCrafting: CustomCrafting): IngredientManager {
            return IngredientManagerCommon(customCrafting)
        }
    }

    fun registerIngredient(key: Key, ingredient: Ingredient)

    fun getIngredient(key: Key): Ingredient?

    fun getKey(ingredient: Ingredient): Key?

}