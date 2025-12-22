package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef

interface IngredientState {

    fun complete(): Result<Ingredient>

    interface CustomIngredientState : IngredientState {

        val stacks: MutableList<ItemStackRef>

        val tags: MutableList<Key>

        var replaceWithRemains: Boolean

    }

    interface SavedIngredientState : IngredientState {

        val key: Key

    }

}