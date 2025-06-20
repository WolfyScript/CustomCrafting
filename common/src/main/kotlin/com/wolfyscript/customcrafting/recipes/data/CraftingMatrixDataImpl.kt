package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import kotlin.math.sqrt

class CraftingMatrixDataImpl(
    private val ingredients: List<ItemStack?>
) : CraftingMatrixData {

    override val originalMatrix: Array<ItemStack?> = ingredients.toTypedArray()

    override val gridSize: Int = when(ingredients.size) {
        4 -> 2
        9 -> 3
        0 -> throw IllegalArgumentException("Cannot create a CraftingMatrixData with no ingredients")
        else -> {
            if (ingredients.size % 2 != 0) throw IllegalArgumentException("Cannot create a CraftingMatrixData with an odd number of ingredients")
            sqrt(ingredients.size.toDouble()).toInt()
        }
    }

    override val items: Array<ItemStack> by lazy { ingredients.filterNotNull().toTypedArray() }
    override val itemIndices: List<Int> by lazy { ingredients.mapIndexedNotNull { i, stack -> if (stack != null && !stack.isEmpty) i else null } }

    override val matrix: Array<ItemStack?>
    override val width: Int
    override val height: Int
    override val rowOffset: Int
    override val columnOffset: Int

    init {
        // Find the leading and trailing empty rows
        var lastRow = gridSize - 1
        var firstRow = 0

        while (firstRow < gridSize && (0 until gridSize).all { originalMatrix[firstRow * gridSize + it] == null }) {
            firstRow++
        }
        while (lastRow > firstRow && (0 until gridSize).all { originalMatrix[lastRow * gridSize + it] == null }) {
            lastRow--
        }

        // Find the leading and trailing empty columns
        var lastCol = gridSize - 1
        var firstCol = 0
        while (firstCol < gridSize && (firstRow until lastRow + 1).all { originalMatrix[it * gridSize + firstCol] == null }) {
            firstCol++
        }
        while (lastCol > firstCol && (firstRow until lastRow + 1).all { originalMatrix[it * gridSize + lastCol] == null }) {
            lastCol--
        }

        // Trim the leading and trailing empty rows and columns
        width = (lastCol + 1) - firstCol
        height = (lastRow + 1) - firstRow
        rowOffset = firstRow
        columnOffset = firstCol
        matrix = Array(width * height) {
            // Copy the values from the original array by offsetting the row and column back to the original
            originalMatrix[(it / width) * gridSize + firstRow * gridSize + (it % width) + firstCol]
        }
    }

}