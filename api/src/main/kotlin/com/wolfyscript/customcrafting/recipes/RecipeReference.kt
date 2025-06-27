package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.scafall.identifier.Key

interface RecipeReference<T: CustomRecipe<*,*>> {

    val key: Key

    val type: RecipeType<*>

    val value: T?

    companion object {

        fun <T: CustomRecipe<*,*>> of(key: Key, recipe: T): RecipeReference<T> {
            return CustomCraftingProvider.get().factories.recipeFactory.createRecipeReference(key, recipe)
        }

    }

}