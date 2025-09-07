package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.scafall.wrappers.utils.wrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

fun List<ItemStack?>.toCraftingMatrixData(): CraftingMatrixData {
    val gridSize: Int = this.gridSize()

    // Find the leading and trailing empty rows
    var lastRow = gridSize - 1
    var firstRow = 0
    var lastCol = gridSize - 1
    var firstCol = 0
    var index = 0
    for (row in 0 until gridSize) {
        var emptyRow = true
        for (column in 0 until gridSize) {
            if (this[index] != null) {
                emptyRow = false
                lastCol = max(lastCol, column)
                firstCol = min(firstCol, column)
            }
            index++
        }
        if (emptyRow) {
            firstRow = min(firstRow, row)
            lastRow = max(lastRow, row)
        }
    }

    // Trim the leading and trailing empty rows and columns
    val width = (lastCol + 1) - firstCol
    val height = (lastRow + 1) - firstRow

    return CraftingMatrixDataImpl(
        this,
        gridSize,
        matrix = Array(width * height) {
            // Copy the values from the original array by offsetting the row and column back to the original
            this[(it / width) * gridSize + firstRow * gridSize + (it % width) + firstCol]
        },
        width,
        height,
        rowOffset = firstRow,
        columnOffset = firstCol
    )
}

private fun List<ItemStack?>.gridSize(): Int {
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
    override val originalMatrix: List<ItemStack?>,
    override val gridSize: Int,
    override val matrix: Array<ItemStack?>,
    override val width: Int,
    override val height: Int,
    override val rowOffset: Int,
    override val columnOffset: Int,
) : CraftingMatrixData {

    override val items: List<ItemStack> by lazy { originalMatrix.filterNotNull() }
    override val itemIndices: List<Int> by lazy { originalMatrix.mapIndexedNotNull { i, stack -> if (stack != null && !stack.isEmpty) i else null } }

    companion object {

        fun of(input: CraftingInput.Positioned, originalItems: List<ItemStack?>) : CraftingMatrixData {
            // Since Vanilla does the same as CustomCrafting would, use the vanilla data.
            // No need to recalculate the trimmed matrix, just add the original ingredient list.
            val craftingInput = input.input
            val columnOffset = input.left
            val rowOffset = input.top
            val items = craftingInput.items()

            return CraftingMatrixDataImpl(
                originalItems,
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
        return "$originalMatrix (size: $gridSize)[${matrix.contentToString()}, ($width x $height) (r: $rowOffset, c: $columnOffset)] (items=$items, indices=$itemIndices)"
    }

}