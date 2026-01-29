package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientModelRef
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ResultModel
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting

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
            collectionIndex: Int,
        )

        fun unassignIngredient(index: Int)

        fun complete(collection: IngredientCollectionModel): Result<T>

        interface Shapeless : CraftingFormulaModel<CraftingFormula.Shapeless> {

            val ingredientRefs: MutableList<IngredientModelRef>

        }

        interface Shaped : CraftingFormulaModel<CraftingFormula.Shaped> {

            val ingredientRefs: MutableList<IngredientModelRef?>

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

