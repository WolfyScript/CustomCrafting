package com.wolfyscript.customcrafting.editor.recipe_stores

import com.wolfyscript.customcrafting.editor.IngredientState
import com.wolfyscript.customcrafting.editor.RecipeState
import com.wolfyscript.customcrafting.editor.result.ResultState
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient

interface RecipeCraftingState : RecipeState.RecipeTypeSpecificState<CustomRecipeCrafting> {

    val result: ResultState

    val formula: CraftingFormulaState<*>

    fun setFormulaType(type: Class<out CraftingFormula>)

    interface CraftingFormulaState<T: CraftingFormula> {

        fun complete(): Result<T>

        interface Shapeless : CraftingFormulaState<CraftingFormula.Shapeless> {

            val ingredients: MutableList<IngredientState>

            /**
             * Adds an ingredient to the end of the [ingredients]
             */
            fun addIngredient(ingredient: Ingredient)

            /**
             * Removes an ingredient from the [ingredients] at the specified index
             */
            fun removeIngredient(index: Int)

        }

        interface Shaped : CraftingFormulaState<CraftingFormula.Shaped> {

            val ingredients: MutableList<IngredientState?>

            /**
             * Assigns an ingredient to the specified index in the recipe
             */
            fun assignIngredient(index: Int, ingredient: Ingredient)

            /**
             * Clears/Unassigns the ingredient at the specified index
             */
            fun clearIngredient(index: Int)

            var shape: ShapeState

            interface ShapeState {

                var symmetry: CraftingFormula.Shaped.ShapeSymmetry

                var trim: Boolean

                fun complete(): Result<CraftingFormula.Shaped.Shape>

            }

        }

    }

}

inline fun <reified T: CraftingFormula> RecipeCraftingState.setFormulaType() {
    setFormulaType(T::class.java)
}