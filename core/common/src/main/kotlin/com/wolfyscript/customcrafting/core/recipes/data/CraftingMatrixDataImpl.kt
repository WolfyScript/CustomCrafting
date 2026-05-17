package com.wolfyscript.customcrafting.core.recipes.data

import com.wolfyscript.scafall.wrappers.minecraft.wrap
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import net.minecraft.world.item.crafting.CraftingInput
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

fun List<ScafallItemStack>.toCraftingMatrixData(): CraftingMatrixData {
    val gridSize: Int = this.gridSize()

    // Find the leading and trailing empty rows
    var minRow = gridSize - 1
    var maxRow = 0
    var minColumn = gridSize - 1
    var maxColumn = 0
    var index = 0
    for (row in 0 until gridSize) {
        var emptyRow = true
        for (column in 0 until gridSize) {
            if (!this[index].unwrap().isEmpty) {
                emptyRow = false
                minColumn = min(minColumn, column)
                maxColumn = max(maxColumn, column)
            }
            index++
        }
        if (!emptyRow) {
            minRow = min(minRow, row)
            maxRow = max(maxRow, row)
        }
    }

    if (maxRow < gridSize - 1 || maxColumn < gridSize - 1 || minRow > 0 || minColumn > 0) {
        // Trim the leading and trailing empty rows and columns
        val width = maxColumn - minColumn + 1
        val height = maxRow - minRow + 1

        return CraftingMatrixDataImpl(
            gridSize,
            matrix = Array(width * height) {
                // Copy the values from the original array by offsetting the row and column back to the original
                this[(it / width) + minRow + (it % width) + maxColumn]
            },
            width,
            height,
            rowOffset = minRow,
            columnOffset = maxColumn
        )
    }
    return CraftingMatrixDataImpl(gridSize(), this.toTypedArray(), gridSize, gridSize, 0, 0)

}

private fun List<ScafallItemStack?>.gridSize(): Int {
    return when (size) {
        4 -> 2
        9 -> 3
        0 -> throw IllegalArgumentException("Cannot create a CraftingMatrixData with no ingredients")
        else -> {
            if (size % 2 != 0) throw IllegalArgumentException("Cannot create a CraftingMatrixData with an odd number of ingredients")
            sqrt(size.toDouble()).toInt()
        }
    }
}

class CraftingMatrixDataImpl(
    override val gridSize: Int,
    override val matrix: Array<ScafallItemStack>,
    override val width: Int,
    override val height: Int,
    override val rowOffset: Int,
    override val columnOffset: Int,
) : CraftingMatrixData {

    override val flatItems: List<ScafallItemStack> = matrix.filter { !it.unwrap().isEmpty }

    override val recipeOffset = rowOffset * gridSize + columnOffset
    override val rowSkip = gridSize - width

    override val itemIndices: List<Int>
    override val flatItemIndices: List<Int>
    init {
        val indices = mutableListOf<Int>()
        val flatIndices = mutableListOf<Int>()
        var index = 0
        for (row in 0 until height) {
            for (col in 0 until width) {
                if (matrix[index] != null) {
                    val ogIndex = index + recipeOffset + row * rowSkip
                    indices.add(ogIndex)
                    flatIndices.add(ogIndex)
                } else {
                    indices.add(-1)
                }
                index++
            }
        }
        itemIndices = indices
        flatItemIndices = flatIndices
    }

    companion object {

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

    override fun toString(): String {
        return "${matrix.contentToString()} (size: $gridSize)[($width x $height) (r: $rowOffset, c: $columnOffset)] ($itemIndices) (flatItems=$flatItems, flatItemIndices=$flatItemIndices)"
    }

}