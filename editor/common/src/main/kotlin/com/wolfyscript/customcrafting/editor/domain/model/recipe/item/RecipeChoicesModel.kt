package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.core.recipes.RecipeChoices
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef

interface RecipeChoicesModel {

    val stacks: MutableList<ItemStackRef>

    val tags: MutableList<Key>

    fun complete(): Result<RecipeChoices>

}