package com.wolfyscript.customcrafting.core.recipe.data

import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient
import com.wolfyscript.scafall.items.ItemStackRef

internal data class IngredientDataImpl(
    override val invSlot: Int,
    override val recipeIndex: Int,
    override val selectedIngredient: Ingredient,
    override val matchedItemStackRef: ItemStackRef
) : IngredientData
