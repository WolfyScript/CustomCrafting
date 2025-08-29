package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.*
import com.wolfyscript.scafall.wrappers.utils.unwrap
import com.wolfyscript.scafall.wrappers.utils.wrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import net.minecraft.util.ArrayListDeque
import org.apache.commons.lang3.ArrayUtils

class CustomRecipeCraftingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val formula: CraftingFormula,
    override val result: RecipeResult,
) : CustomRecipeCrafting {

    override fun evaluate(
        input: RecipeInput.CraftingRecipeInput,
        context: EvaluationContext,
    ): RecipeEvaluationResult.Data? {
        if (!conditions.areSatisfied(context)) {
            return null
        }

        return formula.evaluate(input, this)
    }

    override fun shrink(
        input: RecipeInput.CraftingRecipeInput,
        recipeEvaluationResult: RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting>,
        context: EvaluationContext,
        count: Int,
        applyStacks: (Int, ItemStack) -> Unit,
    ) {
        for (value in recipeEvaluationResult.data.nonNullIngredients) {
            var stack = input.matrixData.originalMatrix[value.invSlot]
            if (stack != null) {
                stack = value.selectedIngredient.shrink(stack, count, value.matchedItemStackRef, context, recipeEvaluationResult)
            }
            if (stack != null) {
                applyStacks(value.invSlot, stack)
            }
        }
    }

    override fun toString(): String {
        return "crafting ($priority) with $formula producing $result if $conditions"
    }

}

class ShapedCraftingFormulaImpl(
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
            val matchedRef = ingredient.match(stack) ?: return null
            val invOffset = if (!shape.trim || matrix.columnOffset == 0 || matrix.rowOffset == 0) {
                0
            } else {
                matrix.rowOffset + matrix.columnOffset * matrix.gridSize + ((i / shape.width) * (matrix.gridSize - matrix.width))
            }
            ingredientData[i] = IngredientDataImpl(
                invSlot = i + invOffset,
                recipeIndex = indexInRecipe,
                selectedIngredient = ingredient,
                matchedItemStackRef = matchedRef
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

        override fun toString(): String {
            return "($rows, ${if (trim) "trimmed, " else ""}$symmetry, ${width}x${height}, ${ingredientIndices}: $variations)"
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

class ShapelessCraftingFormulaImpl(
    override val ingredients: List<Ingredient>,
) : CraftingFormula.Shapeless {

    override fun evaluate(
        input: RecipeInput.CraftingRecipeInput,
        recipeCrafting: CustomRecipeCrafting,
    ): RecipeEvaluationResult.Data? {
        if (input.matrixData.items.size != ingredients.size) {
            return null
        }
        val pickedIngredients = Array<IngredientData?>(ingredients.size) { null }

        /**
         * The path of edges that were visited so far.
         * The indices of ingredients are offset by 1 due to the root node being at index 0.
         */
        val path = ArrayListDeque<Int>(ingredients.size)

        /**
         * A matrix specifying which edges between ingredients have been checked.
         * This includes a root node from which an edge goes to each ingredient.
         *
         *
         * | from \ to | A | B | C | ...
         * | :-------: | - | :--: | :--: | -
         * | ROOT      | 0 | 0 | 0 | ...
         * | A         | x | 0 | 0 | ...
         * | B         | 0 | x | 0 | ...
         * | C         | 0 | 0 | x | ...
         * | ...       | ... | ... | ... | ...
         *
         */
        val checkedEdges: Array<Int> = Array(ingredients.size + 1) { 0 }

        var invItemIndex = 0
        while (invItemIndex < input.matrixData.items.size) {
            val edgeFrom = path.peek() ?: 0 // If path is empty we are at the root

            // Try to match the ingredient at the current index
            for ((ingredientIndex, ingredient) in ingredients.withIndex()) {
                val edgeTo = 1 shl ingredientIndex
                if (checkedEdges[edgeFrom].and(edgeTo) == edgeTo || path.contains(ingredientIndex + 1)) {
                    continue
                }
                val matchedRef = ingredient.match(input.matrixData.items[invItemIndex]) ?: continue
                // Found matching ingredient
                pickedIngredients[ingredientIndex] = IngredientDataImpl(
                    invSlot = input.matrixData.itemIndices[invItemIndex] + input.matrixData.rowOffset * input.matrixData.gridSize + input.matrixData.columnOffset,
                    recipeIndex = ingredientIndex,
                    selectedIngredient = ingredient,
                    matchedItemStackRef = matchedRef
                )
                invItemIndex++
                checkedEdges[edgeFrom] = checkedEdges[edgeFrom].or(edgeTo)
                path.push(ingredientIndex + 1)
                break
            }
            // If it either fails on the first item or backtracks back to the root node, then there are no ingredients left to match.
            if (path.isEmpty()) {
                return null
            }
            if (path.peek() == edgeFrom) {
                // No matching node found. Backtrack
                path.pop()
                pickedIngredients[edgeFrom - 1] = null
                invItemIndex--
            }
        }

        // Make sure all ingredients are on the path, that should be the case already, so simply check for size
        if (path.size == ingredients.size) {
            return DefaultDataImpl(pickedIngredients)
        }
        return null
    }

    override fun toString(): String {
        return "shapeless $ingredients"
    }

}
