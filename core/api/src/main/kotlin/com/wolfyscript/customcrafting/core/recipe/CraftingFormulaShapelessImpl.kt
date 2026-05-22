package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.data.DefaultDataImpl
import com.wolfyscript.customcrafting.core.recipe.data.IngredientData
import com.wolfyscript.customcrafting.core.recipe.data.IngredientDataImpl
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient
import net.minecraft.util.ArrayListDeque

internal class CraftingFormulaShapelessImpl(
    override val ingredients: List<Ingredient>,
) : CraftingFormula.Shapeless {

    override fun evaluate(
        input: RecipeInput.CraftingRecipeInput,
        recipeCrafting: CustomRecipeCrafting,
    ): RecipeEvaluationResult.Data? {
        if (input.matrixData.flatItems.size != ingredients.size) {
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

        var index = 0
        while (index < input.matrixData.flatItems.size) {
            val edgeFrom = path.peek() ?: 0 // If path is empty we are at the root

            // Try to match the ingredient at the current index
            for ((ingrdRecipeIndex, ingredient) in ingredients.withIndex()) {
                val edgeTo = 1 shl ingrdRecipeIndex
                if (checkedEdges[edgeFrom].and(edgeTo) == edgeTo || path.contains(ingrdRecipeIndex + 1)) {
                    continue
                }
                val matchedRef = ingredient.match(input.matrixData.flatItems[index]) ?: continue
                // Found matching ingredient
                pickedIngredients[ingrdRecipeIndex] = IngredientDataImpl(
                    invSlot = input.matrixData.flatItemIndices[index],
                    ingrdRecipeIndex,
                    ingredient,
                    matchedRef
                )
                index++
                checkedEdges[edgeFrom] = checkedEdges[edgeFrom].or(edgeTo)
                path.push(ingrdRecipeIndex + 1)
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
                index--
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