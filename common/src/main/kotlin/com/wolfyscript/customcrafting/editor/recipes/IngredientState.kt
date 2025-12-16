package com.wolfyscript.customcrafting.editor.recipes

import com.wolfyscript.customcrafting.editor.IngredientStore
import com.wolfyscript.customcrafting.recipes.IngredientImpl
import com.wolfyscript.customcrafting.recipes.RecipeChoicesImpl
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

class IngredientStateImpl : IngredientStore {

    override val stacks: MutableList<ScafallItemStack> = mutableListOf()
    override val tags: MutableList<Key> = mutableListOf()
    override var replaceWithRemains: Boolean = true

    override fun complete(): Result<Ingredient> {
        if (stacks.isEmpty() && tags.isEmpty()) {
            return Result.failure(IllegalArgumentException("Ingredient must have at least one stack or tag."))
        }
        val stackRefs = mutableListOf<ItemStackRef>()
        for (stack in stacks) {
            stackRefs.add(ItemStackRef.parse(stack) ?: ItemStackRef.create(stack))
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