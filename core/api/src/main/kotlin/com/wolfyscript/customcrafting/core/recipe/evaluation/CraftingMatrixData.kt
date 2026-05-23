package com.wolfyscript.customcrafting.core.recipe.evaluation

import com.wolfyscript.scafall.wrappers.minecraft.wrap
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import net.minecraft.world.item.crafting.CraftingInput

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
    val matrix: Array<ScafallItemStack>

    val itemIndices: List<Int>

    /**
     * All the items in the crafting grid in order of appearance without empty slots.
     */
    val flatItems: List<ScafallItemStack>

    /**
     * The indices of the items in the matrix. in order of appearance without empty slots.
     * These indices can be associated with [flatItems].
     */
    val flatItemIndices: List<Int>

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

    val recipeOffset: Int

    val rowSkip: Int

    companion object {

        @JvmStatic
        fun of(ingredients: List<ScafallItemStack>): CraftingMatrixData = ingredients.toCraftingMatrixData()

        @JvmStatic
        fun of(input: CraftingInput.Positioned, originalItems: List<ScafallItemStack>): CraftingMatrixData {
            // Since Vanilla does the same as CustomCrafting would, use the vanilla data.
            // No need to recalculate the trimmed matrix, just add the original ingredient list.
            val craftingInput = input.input
            val columnOffset = input.left
            val rowOffset = input.top
            val items = craftingInput.items()

            return CraftingMatrixDataImpl(
                originalItems.gridSize(),
                matrix = Array(craftingInput.size()) { items[it].wrap() },
                craftingInput.width(),
                craftingInput.height(),
                rowOffset,
                columnOffset
            )
        }

    }
}