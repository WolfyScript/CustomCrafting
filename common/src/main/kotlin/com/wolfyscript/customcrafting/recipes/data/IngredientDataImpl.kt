package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.items.ItemStackRef

data class IngredientDataImpl(
    override val invSlot: Int,
    override val recipeIndex: Int,
    override val selectedIngredient: Ingredient,
    override val matchedItemStackRef: ItemStackRef
) : IngredientData
