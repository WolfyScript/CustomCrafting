package com.wolfyscript.customcrafting.editor.domain.usecase

import com.wolfyscript.customcrafting.editor.EditorSession
import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeCraftingModel
import com.wolfyscript.customcrafting.editor.domain.recipes.result.ResultModel
import com.wolfyscript.customcrafting.editor.ui.withCraftingModel
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.ShapedCraftingFormulaImpl

interface RecipeCraftingUseCases {

    interface IngredientCollection {

        class Get(val session: EditorSession) : IngredientUseCases.GetIngredientListUseCase {

            fun getCollection(): RecipeCraftingModel.IngredientCollectionModel = withCraftingModel(session) {
                it.ingredientCollection
            }

            override fun get(): List<IngredientModel> = withCraftingModel(session) {
                it.ingredientCollection.ingredients
            }

        }

        class Set(
            val session: EditorSession,
            val getIngredientCollectionUseCase: Get,
        ) : IngredientUseCases.SetIngredientAtUseCase {

            override fun set(
                index: Int,
                model: IngredientModel,
            ) = withCraftingModel(session) {
                val ingredients = getIngredientCollectionUseCase.getCollection()
                ingredients.set(index, model)
            }

        }

        class Add(
            val session: EditorSession,
            val getIngredientCollectionUseCase: Get,
        ) {

            fun add(toAdd: IngredientModel) = withCraftingModel(session) {
                val ingredients = getIngredientCollectionUseCase.getCollection()
                ingredients.add(toAdd)
            }

        }

        class RemoveIngredientUseCase(val session: EditorSession) {

            fun remove(index: Int) = withCraftingModel(session) { model ->
                model.ingredientCollection.remove(index)
            }
        }

    }

    interface Formula {

        class Get(val session: EditorSession) {

            fun get(): RecipeCraftingModel.CraftingFormulaModel<*> = withCraftingModel(session) {
                it.formula
            }

        }

        class SetType(val session: EditorSession) {

            fun set(type: Class<out CraftingFormula>) = withCraftingModel(session) {
                it.setFormulaType(type)
            }

        }

        class AssignIngredient(val session: EditorSession) {

            fun assign(index: Int, ingredient: IngredientModel) = withCraftingModel(session) { model ->
                model.formula.assignIngredient(index, ingredient)
            }
        }

        class UnassignIngredient(val session: EditorSession) {

            fun unassign(index: Int) = withCraftingModel(session) { model ->
                model.formula.unassignIngredient(index)
            }

        }

        class ToggleTrimShape(val session: EditorSession) {

            fun toggle() = withCraftingModel(session) { model ->
                val formula = model.formula
                if (formula is RecipeCraftingModel.CraftingFormulaModel.Shaped) {
                    formula.shape.trim = !formula.shape.trim
                }
            }

        }

        class ToggleShapeSymmetry(val session: EditorSession) {

            fun toggle(horizontal: Boolean = false, vertical: Boolean = false, rotate: Boolean = false) =
                withCraftingModel(session) {
                    val formula = it.formula
                    if (formula is RecipeCraftingModel.CraftingFormulaModel.Shaped) {
                        val old = formula.shape.symmetry
                        formula.shape.symmetry = ShapedCraftingFormulaImpl.ShapeSymmetryImpl(
                            old.horizontal.xor(horizontal),
                            old.vertical.xor(vertical),
                            old.rotate.xor(rotate)
                        )
                    }
                }

        }

    }

    interface Result {

        class Get(val session: EditorSession) : RecipeResultUseCases.GetResultUseCase {

            override fun get(): ResultModel = withCraftingModel(session) { model ->
                model.result
            }

        }

        class Set(val session: EditorSession) : RecipeResultUseCases.SetResultUseCase {

            override fun set(result: ResultModel) = withCraftingModel(session) { model ->
                model.result = result
            }

        }

    }

}