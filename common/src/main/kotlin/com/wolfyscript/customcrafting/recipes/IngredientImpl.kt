package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class IngredientImpl(
    override val choices: RecipeChoices,
    override val replaceWithRemains: Boolean = false,
    override val matchTags: Boolean = true,
) : Ingredient {

    override fun match(
        stack: ItemStack,
        exact: Boolean,
    ): ItemStackRef? {
        if (stack.amount <= 0 || stack.item.value == "air") {
            return null
        }
        choices.all().forEach {
            if (it.matches(stack, exact)) {
                return it
            }
        }
        return null
    }

    override fun shrink(
        stack: ItemStack,
        amount: Int,
    ): ItemStack {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "{$choices, $replaceWithRemains}"
    }

}