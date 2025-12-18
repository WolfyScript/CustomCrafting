package com.wolfyscript.customcrafting.editor.recipes

import com.wolfyscript.customcrafting.editor.IngredientState
import com.wolfyscript.customcrafting.recipes.IngredientImpl
import com.wolfyscript.customcrafting.recipes.RecipeChoicesImpl
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef

class IngredientStateImpl: IngredientState {

    companion object {

        fun loadFrom(ingredient: Ingredient) : IngredientStateImpl {
            val state = IngredientStateImpl()
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