package com.wolfyscript.customcrafting.recipes

import com.google.common.collect.Multimaps
import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.identifier.Key
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

class RecipeManagerCommon(val customCraftingCommon: CustomCraftingCommon) : RecipeManager {

    private val recipesByType = Multimaps.newListMultimap(Object2ReferenceOpenHashMap<RecipeType<*>, Collection<CustomRecipe<*,*>>>()) { mutableListOf() }

    override val disabledRecipes: MutableSet<Key> = ObjectOpenHashSet()

    fun indexRecipes() {
        recipesByType.clear()
        for (recipe in CustomCraftingRegistryTypes.customRecipes.resolveOrThrow()) {
            recipesByType.put(recipe.type, recipe)
        }
    }

    inline fun <reified T: CustomRecipe<*,*>> getRecipeTyped(key: Key, type: RecipeType<T>): T? {
        val recipe = CustomCraftingRegistryTypes.customRecipes.resolveOrThrow()[key] ?: return null
        if (recipe.type != type) {
            return null
        }
        return recipe as T
    }

    private fun <I: RecipeInput, T: CustomRecipe<I,T>> byType(type: RecipeType<T>): Collection<T> {
        return recipesByType.get(type) as Collection<T>
    }

    override fun <I: RecipeInput, T: CustomRecipe<I,T>> evaluateRecipesOfType(type: RecipeType<T>, input: I, context: EvaluationContext): RecipeData<T>? {
        val recipes: Collection<T> = byType(type)
        for (recipe in recipes) {
            return recipe.evaluate(input, context) ?: continue
        }
        return null
    }

    override fun disableRecipe(recipe: CustomRecipe<*,*>) {
        CustomCraftingRegistryTypes.customRecipes.resolveOrThrow().getKey(recipe)?.let {
            disabledRecipes.add(it)
        }
    }

    override fun enableRecipe(key: Key) {
        disabledRecipes.remove(key)
    }

    override fun getRecipe(key: Key): CustomRecipe<*,*>? {
        return CustomCraftingRegistryTypes.customRecipes.resolveOrThrow()[key]
    }

    override fun removeRecipe(key: Key) {

    }

    override fun updateRecipe(
        key: Key,
        recipe: CustomRecipe<*,*>,
    ) {
        TODO("Not yet implemented")
    }

}