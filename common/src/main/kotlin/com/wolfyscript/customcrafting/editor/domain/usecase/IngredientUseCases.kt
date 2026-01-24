package com.wolfyscript.customcrafting.editor.domain.usecase

import com.wolfyscript.customcrafting.editor.domain.model.CustomIngredientModelImpl
import com.wolfyscript.customcrafting.editor.domain.model.RecipeChoicesModelImpl
import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientMatcherModel
import com.wolfyscript.scafall.identifier.Key
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

        class Get(
            val getIngredientUseCase: GetIngredientByIndexUseCase,
        ) {

            fun get(ingredientIndex: Int): List<ItemStackRef> {
                val ingredient = getIngredientUseCase.get(ingredientIndex)
                if (ingredient is IngredientModel.CustomIngredientModel) {
                    return ingredient.choices.stacks.toList()
                }
                return emptyList()
            }

        }

        class Add(
            val getIngredientUseCase: GetIngredientByIndexUseCase,
            val setIngredientUseCase: SetIngredientAtUseCase,
        ) {

            fun add(ingredientIndex: Int, stack: ItemStackRef) {
                val ingredient = getIngredientUseCase.get(ingredientIndex)
                if (ingredient is IngredientModel.CustomIngredientModel) {
                    val stacks = ingredient.choices.stacks.toMutableList()
                    stacks.add(stack)
                    val updated = CustomIngredientModelImpl(
                        ingredient.replaceWithRemains,
                        RecipeChoicesModelImpl(stacks, ingredient.choices.tags)
                    )
                    setIngredientUseCase.set(ingredientIndex, updated)
                }
            }
        }

        class Remove(
            val getIngredientUseCase: GetIngredientByIndexUseCase,
            val setIngredientUseCase: SetIngredientAtUseCase,
        ) {

            fun remove(ingredientIndex: Int, index: Int) {
                val ingredient = getIngredientUseCase.get(ingredientIndex)
                if (ingredient is IngredientModel.CustomIngredientModel) {
                    val stacks = ingredient.choices.stacks.toMutableList()
                    stacks.removeAt(index)
                    val updated = CustomIngredientModelImpl(
                        ingredient.replaceWithRemains,
                        RecipeChoicesModelImpl(stacks, ingredient.choices.tags)
                    )
                    setIngredientUseCase.set(ingredientIndex, updated)
                }
            }

        }

        class Set(
            val getIngredientUseCase: GetIngredientByIndexUseCase,
            val setIngredientUseCase: SetIngredientAtUseCase,
        ) {

            fun set(ingredientIndex: Int, index: Int, stack: ItemStackRef) {
                val ingredient = getIngredientUseCase.get(ingredientIndex)
                if (ingredient is IngredientModel.CustomIngredientModel) {
                    val stacks = ingredient.choices.stacks.toMutableList()
                    stacks[index] = stack
                    val updated = CustomIngredientModelImpl(
                        ingredient.replaceWithRemains,
                        RecipeChoicesModelImpl(stacks, ingredient.choices.tags)
                    )
                    setIngredientUseCase.set(ingredientIndex, updated)
                }
            }
        }

    }

    interface Tags {

        class Get(
            val getIngredientUseCase: GetIngredientByIndexUseCase,
        ) {

            fun get(ingredientIndex: Int): List<Key> {
                val ingredient = getIngredientUseCase.get(ingredientIndex)
                if (ingredient is IngredientModel.CustomIngredientModel) {
                    return ingredient.choices.tags.toList()
                }
                return emptyList()
            }

        }

        class Add(
            val getIngredientUseCase: GetIngredientByIndexUseCase,
            val setIngredientUseCase: SetIngredientAtUseCase,
        ) {

            fun add(ingredientIndex: Int, tag: Key) {
                val ingredient = getIngredientUseCase.get(ingredientIndex)
                if (ingredient is IngredientModel.CustomIngredientModel) {
                    val tags = ingredient.choices.tags.toMutableList()
                    if (!tags.contains(tag)) {
                        tags.add(tag)
                    }
                    val updated = CustomIngredientModelImpl(
                        ingredient.replaceWithRemains,
                        RecipeChoicesModelImpl(ingredient.choices.stacks, tags)
                    )
                    setIngredientUseCase.set(ingredientIndex, updated)
                }
            }
        }

        class Remove(
            val getIngredientUseCase: GetIngredientByIndexUseCase,
            val setIngredientUseCase: SetIngredientAtUseCase,
        ) {

            fun remove(ingredientIndex: Int, index: Int) {
                val ingredient = getIngredientUseCase.get(ingredientIndex)
                if (ingredient is IngredientModel.CustomIngredientModel) {
                    val tags = ingredient.choices.tags.toMutableList()
                    tags.removeAt(index)
                    val updated = CustomIngredientModelImpl(
                        ingredient.replaceWithRemains,
                        RecipeChoicesModelImpl(ingredient.choices.stacks, tags)
                    )
                    setIngredientUseCase.set(ingredientIndex, updated)
                }
            }

            fun remove(ingredientIndex: Int, key: Key) {
                val ingredient = getIngredientUseCase.get(ingredientIndex)
                if (ingredient is IngredientModel.CustomIngredientModel) {
                    val tags = ingredient.choices.tags.toMutableList()
                    tags.remove(key)
                    val updated = CustomIngredientModelImpl(
                        ingredient.replaceWithRemains,
                        RecipeChoicesModelImpl(ingredient.choices.stacks, tags)
                    )
                    setIngredientUseCase.set(ingredientIndex, updated)
                }
            }

        }

    }

    interface Matcher {

        class Get(val getIngredientUseCase: GetIngredientByIndexUseCase) {

            fun get(ingredientIndex: Int): IngredientMatcherModel<*>? {
                val ingredientModel = getIngredientUseCase.get(ingredientIndex)
                if (ingredientModel is IngredientModel.CustomIngredientModel) {
                    return ingredientModel.matcher
                }
                return null
            }

        }

        class Set(
            val getIngredientUseCase: GetIngredientByIndexUseCase,
            val setIngredientUseCase: SetIngredientAtUseCase
        ) {

            fun set(ingredientIndex: Int, matcher: IngredientMatcherModel<*>) {
                val ingredient = getIngredientUseCase.get(ingredientIndex)
                if (ingredient is IngredientModel.CustomIngredientModel) {
                    val updated = CustomIngredientModelImpl(
                        ingredient.replaceWithRemains,

                    )

                }

            }

        }

    }

    interface Consumer {

        class Get(val getIngredientUseCase: GetIngredientByIndexUseCase) {

            fun get(ingredientIndex: Int): IngredientConsumerModel<*>? {
                val ingredientModel = getIngredientUseCase.get(ingredientIndex)
                if (ingredientModel is IngredientModel.CustomIngredientModel) {
                    return ingredientModel.consumer
                }
                return null
            }

        }

    }

}