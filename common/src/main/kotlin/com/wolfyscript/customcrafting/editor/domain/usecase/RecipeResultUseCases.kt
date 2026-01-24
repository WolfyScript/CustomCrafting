package com.wolfyscript.customcrafting.editor.domain.usecase

import com.wolfyscript.customcrafting.editor.domain.model.RecipeChoicesModelImpl
import com.wolfyscript.customcrafting.editor.domain.model.ResultModelImpl
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.ResultModel
import com.wolfyscript.scafall.items.ItemStackRef

interface RecipeResultUseCases {

    interface GetResultUseCase {

        fun get(): ResultModel

    }

    interface SetResultUseCase {

        fun set(result: ResultModel)

    }

    interface Choices {

        class Add(
            val getResultUseCase: GetResultUseCase,
            val setResultUseCase: SetResultUseCase
        ) {

            fun add(stack: ItemStackRef) {
                val result = getResultUseCase.get()
                val stacksCopy = result.choices.stacks.toMutableList()
                stacksCopy.add(stack)
                setResultUseCase.set(
                    ResultModelImpl(
                        choices = RecipeChoicesModelImpl(stacks = stacksCopy, tags = result.choices.tags.toMutableList()),
                        actions = result.actions.toMutableList(),
                        modifier = result.modifier,
                    )
                )
            }

        }

        class Set(
            val getResultUseCase: GetResultUseCase,
            val setResultUseCase: SetResultUseCase
        ) {

            fun set(index: Int, stack: ItemStackRef) {
                val result = getResultUseCase.get()
                val stacksCopy = result.choices.stacks.toMutableList()
                if (index < stacksCopy.size) {
                    stacksCopy[index] = stack
                    setResultUseCase.set(
                        ResultModelImpl(
                            choices = RecipeChoicesModelImpl(stacks = stacksCopy, tags = result.choices.tags.toMutableList()),
                            actions = result.actions.toMutableList(),
                            modifier = result.modifier,
                        )
                    )
                }
            }

        }

        class Remove(
            val getResultUseCase: GetResultUseCase,
            val setResultUseCase: SetResultUseCase
        ) {

            fun remove(index: Int) {
                val result = getResultUseCase.get()
                val stacksCopy = result.choices.stacks.toMutableList()
                if (index < stacksCopy.size) {
                    stacksCopy.removeAt(index)
                    setResultUseCase.set(
                        ResultModelImpl(
                            choices = RecipeChoicesModelImpl(stacks = stacksCopy, tags = result.choices.tags.toMutableList()),
                            actions = result.actions.toMutableList(),
                            modifier = result.modifier,
                        )
                    )
                }
            }

        }

    }

}