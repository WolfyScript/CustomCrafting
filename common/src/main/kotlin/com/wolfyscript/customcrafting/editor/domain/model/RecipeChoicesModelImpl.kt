package com.wolfyscript.customcrafting.editor.domain.model

import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeChoicesModel
import com.wolfyscript.customcrafting.recipes.RecipeChoices
import com.wolfyscript.customcrafting.recipes.RecipeChoicesImpl
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef

class RecipeChoicesModelImpl(
    override val stacks: MutableList<ItemStackRef> = mutableListOf(),
    override val tags: MutableList<Key> = mutableListOf()
) : RecipeChoicesModel {

    override fun complete(): Result<RecipeChoices> {
        if (stacks.isEmpty() && tags.isEmpty()) {
            return Result.failure(IllegalArgumentException("Recipe Choices must have at least one stack or tag."))
        }

        return Result.success(RecipeChoicesImpl(stacks.toList(), tags.toList()))
    }
}