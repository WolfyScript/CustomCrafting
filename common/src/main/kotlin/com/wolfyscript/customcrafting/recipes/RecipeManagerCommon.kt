package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.identifier.Key
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

class RecipeManagerCommon(val customCraftingCommon: CustomCraftingCommon) : RecipeManager {

    val index: RecipeIndex = RecipeIndex()

    /**
     * Recipes can be loaded by other plugins. We keep track of which recipes CC registers, to not unload third-party recipes, for example, on a reload.
     */
    val recipesLoadedByCC: MutableSet<Key> = ObjectOpenHashSet()

    override val disabledRecipes: MutableSet<Key> = ObjectOpenHashSet()

    override fun <I: RecipeInput, D: RecipeEvaluationResult.Data, T: CustomRecipe<I,D>> evaluateRecipesOfType(type: RecipeType<T>, input: I, context: EvaluationContext): RecipeEvaluationResult<D,T>? {
        val recipes: Collection<RecipeReference<T>> = index.byType(type)
        for (recipe in recipes) {
            val data = recipe.value?.evaluate(input, context) ?: continue
            return RecipeEvaluationResultImpl(recipe, data)
        }
        return null
    }

    override fun disableRecipe(recipe: Key) {
        val ref = index.get(recipe)
        if (ref != null) {
            disabledRecipes.add(recipe)
        }
    }

    override fun enableRecipe(key: Key) {
        disabledRecipes.remove(key)
    }

    fun registerRecipe(key: Key, recipe: CustomRecipe<*,*>) {
        if (key.namespace != Key.CUSTOMCRAFTING_NAMESPACE) {
            return
        }
        recipesLoadedByCC.add(key)
        index.register(key, recipe)
    }

    override fun getRecipe(key: Key): CustomRecipe<*,*>? {
        return index.get(key)?.value
    }

    override fun removeRecipe(key: Key) {
        if (key.namespace != Key.CUSTOMCRAFTING_NAMESPACE) {
            return
        }
        index.remove(key)
    }

    override fun updateRecipe(
        key: Key,
        recipe: CustomRecipe<*,*>,
    ) {
        if (index.byKey.containsKey(key)) {
            removeRecipe(key)
        }
        registerRecipe(key, recipe)
    }

}