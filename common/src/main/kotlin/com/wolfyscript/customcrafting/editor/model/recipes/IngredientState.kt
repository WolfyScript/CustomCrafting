package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.editor.model.recipes.IngredientState
import com.wolfyscript.customcrafting.recipes.IngredientImpl
import com.wolfyscript.customcrafting.recipes.RecipeChoicesImpl
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef

class CustomIngredientStateImpl : IngredientState.CustomIngredientState {

    companion object {

        fun loadFrom(ingredient: Ingredient) : CustomIngredientStateImpl {
            val state = CustomIngredientStateImpl()
            // TODO: properly clone values!
            state.stacks.addAll(ingredient.choices.stacks)
            state.tags.addAll(ingredient.choices.tags)

            return state
        }

    }

    override val stacks: MutableList<ItemStackRef> = mutableListOf()
    override val tags: MutableList<Key> = mutableListOf()
    override var replaceWithRemains: Boolean = true

    override fun complete(): Result<Ingredient> {
        if (stacks.isEmpty() && tags.isEmpty()) {
            return Result.failure(IllegalArgumentException("Ingredient must have at least one stack or tag."))
        }
        val stackRefs = mutableListOf<ItemStackRef>()
        for (stack in stacks) {
            stackRefs.add(stack)
        }

        return Result.success(
            IngredientImpl(
                RecipeChoicesImpl(
                    stackRefs,
                    tags
                )
            )
        )
    }

}

class SavedIngredientStateImpl(override val key: Key) : IngredientState.SavedIngredientState {

    override fun complete(): Result<Ingredient> {
        TODO("Not yet implemented")
    }

}