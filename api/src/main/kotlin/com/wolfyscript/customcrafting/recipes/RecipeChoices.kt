package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef

interface RecipeChoices {

    val stacks: List<ItemStackRef>

    val tags: List<Key>

    fun all(): List<ItemStackRef>

    fun allFor(context: EvaluationContext): List<ItemStackRef>

}