package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef

interface IngredientState {

    val stacks: MutableList<ItemStackRef>

    val tags: MutableList<Key>

    var replaceWithRemains: Boolean

    fun complete(): Result<Ingredient>

}