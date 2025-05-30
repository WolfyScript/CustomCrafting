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

    override val matrix: Array<ItemStack?>
    override val width: Int
    override val height: Int
    override val rowOffset: Int
    override val columnOffset: Int

    init {
        // Find the leading and trailing empty rows
        var rMax = gridSize
        var rMin = 0
        while ((0 until gridSize).all { originalMatrix[rMin * gridSize + it] == null }) {
            rMin++
        }
        while ((0 until gridSize).all { originalMatrix[rMax * gridSize + it] == null }) {
            rMax--
        }

        // Find the leading and trailing empty columns
        var cMax = gridSize
        var cMin = 0
        while ((rMin until rMax).all { originalMatrix[it * gridSize + cMin] == null }) {
            cMin++
        }
        while ((rMin until rMax).all { originalMatrix[it * gridSize + cMax] == null }) {
            cMax--
        }

        // Trim the leading and trailing empty rows and columns
        width = cMax - cMin
        height = rMax - rMin
        rowOffset = rMin
        columnOffset = cMin
        matrix = Array(width * height) {
            // Copy the values from the original array by offsetting the row and column back to the original
            // <Row in trimmed shape> + rMin = <Row in original shape>
            // <Column in trimmed shape> + cMin = <Column in original shape>
            originalMatrix[(it / width) + rMin + (it % width) + cMin]
        }
    }

}