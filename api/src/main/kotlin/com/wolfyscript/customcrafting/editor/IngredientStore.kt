package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.recipes.Ingredient
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface IngredientStore {

    val stacks: MutableList<ItemStack>

    val tags: MutableList<Key>

    var replaceWithRemains: Boolean

    fun complete(): Result<Ingredient>

}