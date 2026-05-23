package com.wolfyscript.customcrafting.core.recipe

import com.fasterxml.jackson.annotation.JsonIgnore
import com.wolfyscript.customcrafting.core.recipe.evaluation.CraftingMatrixData
import com.wolfyscript.customcrafting.core.recipe.evaluation.DefaultDataImpl
import com.wolfyscript.customcrafting.core.recipe.evaluation.IngredientData
import com.wolfyscript.customcrafting.core.recipe.evaluation.IngredientDataImpl
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient
import org.apache.commons.lang3.ArrayUtils
import kotlin.math.max
import kotlin.math.min

internal class CraftingFormulaShapedImpl(
    val mappedIngredients: Map<Char, Ingredient>,
    override val shape: CraftingFormula.Shaped.Shape,
) : CraftingFormula.Shaped {

    /**
     * Converts the mapped ingredients to a more efficient lookup using indices instead.
     * This constructs a list of Ingredients by replacing the [shapes][shape] ingredient indices with their corresponding ingredient.
     */
    @JsonIgnore
    override val ingredients: List<Ingredient> = shape.ingredientIndices.map {
        mappedIngredients[it] ?: throw IllegalArgumentException("No ingredient for character '$it'")
    }

    override fun evaluate(
        input: RecipeInput.CraftingRecipeInput,
        recipeCrafting: CustomRecipeCrafting,
    ): RecipeEvaluationResult.Data? {
        for (variant in shape.variations) {
            val result = evaluateShape(input.matrixData, variant, recipeCrafting)
            if (result != null) {
                return result
            }
        }
        return null
    }

    private fun evaluateShape(
        matrix: CraftingMatrixData,
        ingredientShape: Array<Int>,
        recipeCrafting: CustomRecipeCrafting,
    ): RecipeEvaluationResult.Data? {
        if (matrix.width != shape.width || matrix.height != shape.height) {
            return null
        }
        val ingredientData: Array<IngredientData?> = Array(matrix.width * shape.height) { null }
        for ((i, stack) in matrix.matrix.withIndex()) {
            val ingrdRecipeIndex = ingredientShape[i]
            if (stack?.isEmpty ?: true) {
                if (ingrdRecipeIndex >= 0) {
                    return null
                }
                continue
            }
            if (ingrdRecipeIndex < 0) {
                return null
            }
            val ingredient = ingredients[ingrdRecipeIndex]
            val matchedRef = ingredient.match(stack) ?: return null
            ingredientData[i] = IngredientDataImpl(
                matrix.itemIndices[i],
                i,
                ingredient,
                matchedRef
            )
        }
        return DefaultDataImpl(ingredientData)
    }

    override fun toString(): String {
        return "shape $shape with $mappedIngredients"
    }

    class ShapeImpl(
        override val rows: List<String>,
        override val symmetry: CraftingFormula.Shaped.ShapeSymmetry,
        override val trim: Boolean = true,
    ) : CraftingFormula.Shaped.Shape {

        override var width: Int = rows.maxOf { it.length }
            private set
        override var height: Int = rows.size
            private set
        override val variations: MutableList<Array<Int>> = mutableListOf()
        override val ingredientIndices: MutableList<Char> = mutableListOf()

        init {
            var original: Array<Int> = Array(height * width) { -1 }
            var index = 0
            var minRow = height - 1
            var maxRow = 0
            var minColumn = width - 1
            var maxColumn = 0
            for ((r, row) in rows.withIndex()) {
                var emptyRow = true
                for ((c, column) in row.withIndex()) {
                    if (column.isWhitespace()) {
                        original[index] = -1
                    } else {
                        emptyRow = false
                        minColumn = min(minColumn, c)
                        maxColumn = max(maxColumn, c)

                        var i = ingredientIndices.indexOf(column)
                        if (i < 0) {
                            ingredientIndices.add(column)
                            i = ingredientIndices.size - 1
                        }
                        original[index] = i
                    }
                    index++
                }

                if (!emptyRow) {
                    minRow = min(minRow, r)
                    maxRow = max(maxRow, r)
                }
            }

            if (trim && (maxRow < width - 1 || maxColumn < height - 1 || minRow > 0 || minColumn > 0)) {
                // Trim the leading and trailing empty rows and columns
                width = maxColumn - minColumn + 1
                height = maxRow - minRow + 1
                var trimmed = Array(width * height) {
                    // Copy the values from the original array by offsetting the row and column back to the original
                    // <Row in trimmed shape> + rMin = <Row in original shape>
                    // <Column in trimmed shape> + cMin = <Column in original shape>
                    original[(it / width) + minRow + (it % width) + minColumn]
                }
                original = trimmed
            }
            variations.add(original)

            if (symmetry.horizontal) {
                val mirroredHorizontally = Array(original.size) { original[it] }
                for (i in 0 until height) {
                    ArrayUtils.reverse(mirroredHorizontally, i * width, (i + 1) * width)
                }
                variations.add(mirroredHorizontally)
            }
            if (symmetry.vertical) {
                val mirroredVertically = Array(original.size) { original[it] }
                var first = 0
                var last = height - 1
                while (first < last) {
                    ArrayUtils.swap(mirroredVertically, first * width, last * width, width)
                    first++
                    last--
                }
                variations.add(mirroredVertically)
            }
            if (symmetry.horizontal && symmetry.vertical && symmetry.rotate) {
                // this is equivalent to mirroring horizontally and vertically
                variations.add(original.reversedArray())
            }
        }

        override fun toString(): String {
            return "($rows, ${if (trim) "trimmed, " else ""}$symmetry, ${width}x${height}, ${ingredientIndices}: ${variations.joinToString { it.contentToString()}})"
        }

    }

    data class ShapeSymmetryImpl(
        override val horizontal: Boolean,
        override val vertical: Boolean,
        override val rotate: Boolean,
    ) : CraftingFormula.Shaped.ShapeSymmetry {

        override fun toString(): String {
            return "(horizontal=$horizontal, vertical=$vertical, rotate=$rotate)"
        }
    }

}