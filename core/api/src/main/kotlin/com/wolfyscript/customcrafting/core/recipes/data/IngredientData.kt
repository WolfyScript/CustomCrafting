package com.wolfyscript.customcrafting.core.recipes.data

import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.items.ItemStackRef

/**
 * Holds information about an ingredient that was selected after a recipe was evaluated.
 */
interface IngredientData {

    /**
     * The slot of the ingredient in the inventory.
     * For example, the slot in the crafting grid
     */
    val invSlot: Int

    /**
     * The index of the ingredient in the recipe
     */
    val recipeIndex: Int

    /**
     * The ingredient associated with this information
     */
    val selectedIngredient: Ingredient

    val matchedItemStackRef: ItemStackRef

}