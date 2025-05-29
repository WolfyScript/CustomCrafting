package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

/**
 * Holds information about the crafting grid matrix and the shape within.
 *
 * This is calculated once before the recipes are evaluated to reduce the number of recipes to evaluate against.
 * It stores both the trimmed and full original matrix. Recipes are evaluated against the trimmed matrix.
 */
interface CraftingMatrixData {

    /**
     * The trimmed matrix, without outer empty rows and columns.
     * That means that this matrix **may be smaller** than the original matrix and **may not be square**.
     */
    val matrix: Array<ItemStack?>

    /**
     * The full original square matrix.
     * The two usual sizes are 2x2 and 3x3.
     */
    val originalMatrix: Array<ItemStack?>

    /**
     * All the items in the crafting grid in order of appearance without empty slots.
     */
    val items: Array<ItemStack>

    /**
     * The size of the crafting grid.
     * For example, a 2x2 grid has a size of 2.
     */
    val gridSize: Int

    /**
     * The width (number of columns) of the trimmed matrix.
     */
    val width: Int

    /**
     * The height (number of rows) of the trimmed matrix.
     */
    val height: Int

    /**
     * The amount of rows this trimmed matrix is offset within the original matrix.
     */
    val rowOffset: Int

    /**
     * The amount of columns this trimmed matrix is offset within the original matrix.
     */
    val columnOffset: Int

    companion object {

        fun of(ingredients: List<ItemStack?>): CraftingMatrixData = CustomCraftingProvider.get().factories.recipeFactory.createMatrixData(ingredients)

    }
}