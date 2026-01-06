package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot

interface IngredientModel {

    fun complete(): Result<Ingredient>

    interface CustomIngredientModel : IngredientModel {

        val stacks: MutableList<ItemStackRef>

        val tags: MutableList<Key>

        val replaceWithRemains: Boolean

    }

    interface SavedIngredientModel : IngredientModel {

        val key: Key

        val icon: ItemStackSnapshot

    }

}