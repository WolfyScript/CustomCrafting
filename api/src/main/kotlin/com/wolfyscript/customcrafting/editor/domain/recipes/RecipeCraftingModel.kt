package com.wolfyscript.customcrafting.editor.domain.recipes

import com.wolfyscript.customcrafting.editor.domain.recipes.result.ResultModel
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient

interface RecipeCraftingModel : RecipeModel.RecipeTypeSpecificModel<CustomRecipeCrafting> {

    val ingredientCollection: IngredientCollectionModel

    var result: ResultModel

    val formula: CraftingFormulaModel<*>

    fun setFormulaType(type: Class<out CraftingFormula>)

    /**
     * Collects the ingredients to be used to construct the formula and prevent duplicate ingredients.
     * Use is entirely optional as ingredients can be added to the formula directly.
     */
    interface IngredientCollectionModel {

        val ingredients: MutableList<IngredientModel>

        fun addNew()

        fun add(ingredient: IngredientModel)

        fun set(index: Int, ingredient: IngredientModel)

        fun remove(index: Int)

    }

    interface CraftingFormulaModel<T: CraftingFormula> {

        fun assignIngredient(
            index: Int,
            ingredient: IngredientModel,
        )

        fun unassignIngredient(index: Int)

        fun getIngredient(index: Int): IngredientModel?

        fun complete(): Result<T>

        interface Shapeless : CraftingFormulaModel<CraftingFormula.Shapeless> {

            val ingredients: MutableList<IngredientModel>

            /**
             * Adds an ingredient to the end of the [ingredients]
             */
            fun addIngredient(ingredient: Ingredient)

            fun addIngredient(ingredient: IngredientModel)

            /**
             * Removes an ingredient from the [ingredients] at the specified index
             */
            fun removeIngredient(index: Int)

        }

        interface Shaped : CraftingFormulaModel<CraftingFormula.Shaped> {

            val ingredients: MutableList<IngredientModel?>

            /**
             * Assigns an ingredient to the specified index in the recipe
             */
            fun assignIngredient(index: Int, ingredient: Ingredient)

            /**
             * Clears/Unassigns the ingredient at the specified index
             */
            fun clearIngredient(index: Int)

            var shape: ShapeModel

            interface ShapeModel {

                var symmetry: CraftingFormula.Shaped.ShapeSymmetry

                var trim: Boolean

                fun complete(): Result<CraftingFormula.Shaped.Shape>

            }

        }

    }

}

inline fun <reified T: CraftingFormula> RecipeCraftingModel.setFormulaType() {
    setFormulaType(T::class.java)
}