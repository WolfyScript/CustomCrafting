package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonCreator
import com.wolfyscript.customcrafting.recipes.data.*
import net.minecraft.util.ArrayListDeque
import org.apache.commons.lang3.ArrayUtils

class CustomRecipeCraftingImpl @JsonCreator constructor(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val formula: CraftingFormula,
    override val result: RecipeResult,
) : CustomRecipeCrafting {

    override fun evaluate(
        matrix: CraftingMatrixData,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeCrafting>? {
        if (!conditions.areSatisfied(context)) {
            return null
        }

        return formula.evaluate(matrix, this)
    }

    override val type: RecipeType<CustomRecipeCrafting>
        get() = TODO("Not yet implemented")
}

class ShapedCraftingFormulaImpl @JsonCreator constructor(
    val mappedIngredients: Map<Char, Ingredient>,
    override val shape: CraftingFormula.Shaped.Shape,
) : CraftingFormula.Shaped {

    /**
     * Converts the mapped ingredients to a more efficient lookup using indices instead.
     * This constructs a list of Ingredients by replacing the [shapes][shape] ingredient indices with their corresponding ingredient.
     */
    override val ingredients: List<Ingredient> = shape.ingredientIndices.map {
        mappedIngredients[it] ?: throw IllegalArgumentException("No ingredient for character '$it'")
    }

    override fun evaluate(
        matrix: CraftingMatrixData,
        recipeCrafting: CustomRecipeCrafting,
    ): RecipeData<CustomRecipeCrafting>? {
        for (variant in shape.variations) {
            val result = evaluateShape(matrix, variant, recipeCrafting)
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
    ): RecipeData<CustomRecipeCrafting>? {
        val ingredientData: Array<IngredientData?> = Array(matrix.width * shape.height) { null }
        for ((i, stack) in matrix.matrix.withIndex()) {
            val indexInRecipe = ingredientShape[i]
            if (stack == null) {
                if (indexInRecipe >= 0) {
                    return null
                }
                continue
            }
            if (indexInRecipe < 0) {
                return null
            }
            val ingredient = ingredients[indexInRecipe]
            val matchedRef = ingredient.match(stack, true) ?: return null
            val invOffset = if (!shape.trim || matrix.columnOffset == 0 || matrix.rowOffset == 0) {
                0
            } else {
                matrix.rowOffset + matrix.columnOffset * matrix.gridSize + ((i / shape.width) * (matrix.gridSize - matrix.width))
            }
            ingredientData[indexInRecipe] = IngredientDataImpl(
                invSlot = i + invOffset,
                recipeIndex = indexInRecipe,
                selectedIngredient = ingredient,
                matchedItemStackRef = matchedRef
            )
        }
        return RecipeDataImpl(recipeCrafting, recipeCrafting.result, ingredientData)
    }


    class ShapeImpl @JsonCreator constructor(
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
            for (row in rows) {
                for (column in row) {
                    original[index] = if (column.isWhitespace()) {
                        -1
                    } else {
                        var i = ingredientIndices.indexOf(column)
                        if (i < 0) {
                            ingredientIndices.add(column)
                            i = ingredientIndices.size - 1
                        }
                        i
                    }
                    index++
                }
            }

            if (trim) {
                // Find the leading and trailing empty rows
                var rMax = height
                var rMin = 0
                while ((0 until width).all { original[rMin * width + it] >= 0 }) {
                    rMin++
                }
                while ((0 until width).all { original[rMax * width + it] >= 0 }) {
                    rMax--
                }

                // Find the leading and trailing empty columns
                var cMax = width
                var cMin = 0
                while ((rMin until rMax).all { original[it * width + cMin] >= 0 }) {
                    cMin++
                }
                while ((rMin until rMax).all { original[it * width + cMax] >= 0 }) {
                    cMax--
                }

                // Trim the leading and trailing empty rows and columns
                width = cMax - cMin
                height = rMax - rMin
                var trimmed = Array(width * height) {
                    // Copy the values from the original array by offsetting the row and column back to the original
                    // <Row in trimmed shape> + rMin = <Row in original shape>
                    // <Column in trimmed shape> + cMin = <Column in original shape>
                    original[(it / width) + rMin + (it % width) + cMin]
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
                    first++;
                    last--;
                }
                variations.add(mirroredVertically)
            }
            if (symmetry.horizontal && symmetry.vertical && symmetry.rotate) {
                // this is equivalent to mirroring horizontally and vertically
                variations.add(original.reversedArray())
            }
        }

    }
}

class ShapelessCraftingFormulaImpl(override val ingredients: List<Ingredient>) : CraftingFormula.Shapeless {

    override fun evaluate(
        matrix: CraftingMatrixData,
        recipeCrafting: CustomRecipeCrafting,
    ): RecipeData<CustomRecipeCrafting>? {
        TODO("Not yet implemented")
    }

}
