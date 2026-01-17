package com.wolfyscript.customcrafting.editor.domain.recipes

import com.wolfyscript.customcrafting.recipes.RecipeChoices
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

interface RecipeChoicesModel {

    val stacks: MutableList<ItemStackRef>

    val tags: MutableList<Key>

    fun complete(): Result<RecipeChoices>

}