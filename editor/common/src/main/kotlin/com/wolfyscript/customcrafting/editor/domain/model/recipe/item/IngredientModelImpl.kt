package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.RecipeChoicesModelImpl
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.RecipeChoicesModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerModels
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherModels
import com.wolfyscript.customcrafting.recipes.IngredientImpl
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumer
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import kotlinx.coroutines.runBlocking

class CustomIngredientModelImpl(
    override var replaceWithRemains: Boolean = true,
    override val choices: RecipeChoicesModel = RecipeChoicesModelImpl(),
    override val matcher: IngredientMatcherModel<*> = IngredientMatcherModels.exact.resolveOrThrow().createEmptyModel(),
    override val consumer: IngredientConsumerModel<*> = IngredientConsumerModels.consume.resolveOrThrow().createEmptyModel(),
) : IngredientModel.CustomIngredientModel {

    companion object {

        fun loadFrom(ingredient: Ingredient): CustomIngredientModelImpl {
            val state = CustomIngredientModelImpl(
                (ingredient.consumption is IngredientConsumer.Consume),
                RecipeChoicesModelImpl(
                    ingredient.choices.stacks.toMutableList(),
                    ingredient.choices.tags.toMutableList(),
                ),
            )
            // TODO: properly clone values!
            return state
        }

    }

    override fun complete(): Result<Ingredient> {
        return runBlocking {
            val recipeChoices = choices.complete().getOrElse {
                return@runBlocking Result.failure(IllegalStateException("Failed to complete Ingredient.", it))
            }

            if (recipeChoices.all().isEmpty()) {
                return@runBlocking Result.failure(IllegalArgumentException("Ingredient must have at least one stack or tag."))
            }

            return@runBlocking Result.success(IngredientImpl(recipeChoices))
        }
    }

}

class SavedIngredientModelImpl(
    override val key: Key,
    override val icon: ItemStackSnapshot,
) : IngredientModel.SavedIngredientModel {

    override fun complete(): Result<Ingredient> {
        val ingredient =
            CustomCraftingProvider.get().server?.ingredientManager?.getIngredient(key) ?: return Result.failure(
                IllegalStateException("Failed to complete Ingredient: No ingredient found for key $key")
            )
        return Result.success(ingredient)
    }

}