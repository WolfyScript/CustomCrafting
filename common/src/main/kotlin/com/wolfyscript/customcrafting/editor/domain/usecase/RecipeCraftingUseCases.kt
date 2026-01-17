package com.wolfyscript.customcrafting.editor.domain.usecase

import com.wolfyscript.customcrafting.editor.EditorSession
import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeCraftingModel
import com.wolfyscript.customcrafting.editor.ui.withCraftingModel
import com.wolfyscript.customcrafting.recipes.CraftingFormula

interface RecipeCraftingUseCases {

    interface IngredientCollection {

        class GetUseCase(val session: EditorSession) : IngredientUseCases.GetIngredientListUseCase {

            fun getCollection(): RecipeCraftingModel.IngredientCollectionModel = withCraftingModel(session) {
                it.ingredientCollection
            }

            override fun get(): List<IngredientModel> = withCraftingModel(session) {
                it.ingredientCollection.ingredients
            }

        }

        class SetIngredientUseCase(
            val session: EditorSession,
            val getIngredientCollectionUseCase: GetUseCase,
        ) : IngredientUseCases.SetIngredientAtUseCase {

            override fun set(
                index: Int,
                model: IngredientModel,
            ) = withCraftingModel(session) {
                val ingredients = getIngredientCollectionUseCase.getCollection()
                ingredients.set(index, model)
            }

        }

        class AddIngredientUseCase(
            val session: EditorSession,
            val getIngredientCollectionUseCase: GetUseCase,
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

        class SetShapeSymmetry(val session: EditorSession) {

            fun set(symmetry: CraftingFormula.Shaped.ShapeSymmetry) = withCraftingModel(session) {
                val formula = it.formula
                if (formula is RecipeCraftingModel.CraftingFormulaModel.Shaped) {
                    formula.shape.symmetry = symmetry
                }
            }

        }

    }

}