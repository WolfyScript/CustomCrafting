package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider

interface RemainsIgnoreOptions {

    companion object {

        fun of(vanilla: Boolean = false, others: Boolean = false) =
            CustomCraftingProvider.get().factories.recipeFactory.ingredient.createRemainsIgnoreOptions(vanilla, others)

    }

    val vanilla: Boolean

    val others: Boolean

}