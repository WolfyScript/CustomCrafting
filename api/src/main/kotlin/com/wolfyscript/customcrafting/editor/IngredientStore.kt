package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

interface IngredientStore {

    val stacks: MutableList<ScafallItemStack>

    val tags: MutableList<Key>

    var replaceWithRemains: Boolean

    fun complete(): Result<Ingredient>

}