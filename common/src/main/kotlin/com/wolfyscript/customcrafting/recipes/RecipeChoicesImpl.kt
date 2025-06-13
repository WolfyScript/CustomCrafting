package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef

class RecipeChoicesImpl(
    override val stacks: List<ItemStackRef> = emptyList(),
    override val tags: List<Key> = emptyList(),
) : RecipeChoices {

    override fun all(): List<ItemStackRef> {
        TODO("Not yet implemented")
    }

    override fun allFor(context: EvaluationContext): List<ItemStackRef> {
        TODO("Not yet implemented")
    }
}