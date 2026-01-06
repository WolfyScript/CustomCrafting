package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.recipes.IngredientImpl
import com.wolfyscript.customcrafting.recipes.RecipeChoicesImpl
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumer
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import kotlinx.coroutines.runBlocking
import net.minecraft.world.item.ItemStack

class CustomIngredientModelImpl(
    override val stacks: MutableList<ItemStackRef> = mutableListOf(),
    override val tags: MutableList<Key> = mutableListOf(),
    override var replaceWithRemains: Boolean = true,
) : IngredientModel.CustomIngredientModel {

    companion object {

        fun loadFrom(ingredient: Ingredient) : CustomIngredientModelImpl {
            val state = CustomIngredientModelImpl(
                ingredient.choices.stacks.toMutableList(),
                ingredient.choices.tags.toMutableList(),
                (ingredient.consumption is IngredientConsumer.Consume)
            )
            // TODO: properly clone values!
            return state
        }

    }

    override fun complete(): Result<Ingredient> {
        return runBlocking {
            val finalStacks = stacks.toMutableList()
            val finalTags = tags.toMutableList()

            if (finalStacks.isEmpty() && finalTags.isEmpty()) {
                return@runBlocking Result.failure(IllegalArgumentException("Ingredient must have at least one stack or tag."))
            }

            return@runBlocking Result.success(
                IngredientImpl(
                    RecipeChoicesImpl(
                        finalStacks,
                        finalTags
                    )
                )
            )
        }
    }

}

class SavedIngredientModelImpl(
    override val key: Key,
    override val icon: ItemStackSnapshot
) : IngredientModel.SavedIngredientModel {

    override fun complete(): Result<Ingredient> {
        CustomCraftingProvider.get().server?.ingredientManager?.let { manager ->
            manager.getIngredient(key)?.choices?.all()?.getOrNull(0)?.create()?.snapshot()
        } ?: ItemStack.EMPTY.snapshot()
        TODO("Not yet implemented")
    }

}