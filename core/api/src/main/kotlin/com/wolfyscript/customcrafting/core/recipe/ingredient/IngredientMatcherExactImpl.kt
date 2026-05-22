package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStackLike

internal class IngredientMatcherExactImpl : IngredientMatcher.Exact {

    override fun match(
        ingredient: Ingredient,
        source: ItemStackLike,
    ): ItemStackRef? {
        return ingredient.choices.all().firstOrNull { choice ->
            choice.matches(source, true)
        }
    }

    override fun toString(): String {
        return "exact"
    }

}