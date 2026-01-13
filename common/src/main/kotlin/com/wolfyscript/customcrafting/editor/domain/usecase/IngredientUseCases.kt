package com.wolfyscript.customcrafting.editor.domain.usecase

import com.wolfyscript.customcrafting.editor.domain.model.CustomIngredientModelImpl
import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.scafall.items.ItemStackRef

interface IngredientUseCases {

    /**
     * A use-case implemented by other more specific use-cases that provide a list of non-null ingredients
     */
    interface GetIngredientListUseCase {

        fun get(): List<IngredientModel>

    }

    interface GetIngredientByIndexUseCase {

        fun get(index: Int): IngredientModel?

    }

    interface SetIngredientAtUseCase {

        fun set(index: Int, model: IngredientModel)

    }

    class GetIngredientUseCase(
        val getIngredientListUseCase: GetIngredientListUseCase,
    ) : GetIngredientByIndexUseCase {

        override fun get(index: Int): IngredientModel? {
            return getIngredientListUseCase.get().getOrNull(index)
        }

    }

    interface Choices {

        class Add(
            val getIngredientUseCase: GetIngredientUseCase,
            val setIngredientUseCase: SetIngredientAtUseCase,
        ) {

            fun add(ingredient: Int, stack: ItemStackRef) {
                val updated = when (val ingredient = getIngredientUseCase.get(ingredient)) {
                    is IngredientModel.CustomIngredientModel -> {
                        val stacks = ingredient.stacks.toMutableList()
                        stacks.add(stack)
                        CustomIngredientModelImpl(stacks, ingredient.tags, ingredient.replaceWithRemains)
                    }

                    is IngredientModel.SavedIngredientModel -> {
                        null
                    }

                    else -> null
                }
                if (updated != null) {
                    setIngredientUseCase.set(ingredient, updated)
                }
            }
        }

        class Remove(
            val getIngredientUseCase: GetIngredientUseCase,
            val setIngredientUseCase: SetIngredientAtUseCase,
        ) {

            fun remove(ingredient: Int, index: Int) {
                val updated = when (val ingredient = getIngredientUseCase.get(ingredient)) {
                    is IngredientModel.CustomIngredientModel -> {
                        val stacks = ingredient.stacks.toMutableList()
                        stacks.removeAt(index)
                        CustomIngredientModelImpl(stacks, ingredient.tags, ingredient.replaceWithRemains)
                    }

                    is IngredientModel.SavedIngredientModel -> {
                        null
                    }

                    else -> null
                }
                if (updated != null) {
                    setIngredientUseCase.set(ingredient, updated)
                }
            }

        }

        class Set(
            val getIngredientUseCase: GetIngredientUseCase,
            val setIngredientUseCase: SetIngredientAtUseCase,
        ) {

            fun set(ingredient: Int, index: Int, stack: ItemStackRef) {
                val updated = when (val ingredient = getIngredientUseCase.get(ingredient)) {
                    is IngredientModel.CustomIngredientModel -> {
                        val stacks = ingredient.stacks.toMutableList()
                        stacks[index] = stack
                        CustomIngredientModelImpl(stacks, ingredient.tags, ingredient.replaceWithRemains)
                    }

                    is IngredientModel.SavedIngredientModel -> {
                        null
                    }

                    else -> null
                }
                if (updated != null) {
                    setIngredientUseCase.set(ingredient, updated)
                }
            }
        }

    }

}