package com.wolfyscript.customcrafting.editor.domain.usecase

import com.wolfyscript.customcrafting.editor.EditorSession
import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeCraftingModel
import com.wolfyscript.customcrafting.editor.ui.withCraftingModel

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

}