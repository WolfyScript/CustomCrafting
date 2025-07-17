package com.wolfyscript.customcrafting.recipes

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.identifier.Key
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import java.util.*

class RecipeIndex {

    private val recipes: MutableSet<CustomRecipe<*, *>> = ObjectOpenHashSet()

    // reference those recipes, but do not hold on to those object references directly
    internal val byKey: MutableMap<Key, RecipeReference<*>> = Object2ObjectOpenHashMap()
    internal val byType: Multimap<RecipeType<*>, RecipeReference<*>> =
        Multimaps.newSetMultimap(Object2ObjectOpenHashMap()) { ObjectOpenHashSet() }

    fun values(): Collection<RecipeReference<*>> {
        return Collections.unmodifiableCollection(byKey.values)
    }

    fun <T : CustomRecipe<*, *>> register(key: Key, recipe: T): RecipeReference<T> = synchronized(this) {
        if (byKey.containsKey(key)) {
            return byKey[key]!! as RecipeReference<T>
        }
        val ref = RecipeReferenceImpl(key, recipe)
        byKey[key] = ref
        byType.put(recipe.type, ref)
        return ref
    }

    fun get(key: Key): RecipeReference<*>? = synchronized(this) {
        return byKey[key]
    }

    fun remove(key: Key) = synchronized(this) {
        val recipeRef = byKey[key]
        if (recipeRef != null) {
            val recipe = recipeRef.value
            recipes.remove(recipe)
            byType.remove(recipeRef.type, recipeRef)
            byKey.remove(key)
        }
    }

    inline fun <reified T : CustomRecipe<*, *>> getRecipeTyped(key: Key, type: RecipeType<T>): T? = synchronized(this) {
        val recipe = get(key) ?: return null
        if (recipe.type != type) {
            return null
        }
        return type.recipeClass.cast(recipe)
    }

    fun <I : RecipeInput, D : RecipeEvaluationResult.Data, T : CustomRecipe<I, D>> byType(type: RecipeType<T>): Collection<RecipeReference<T>> = synchronized(this) {
        return byType.get(type) as Collection<RecipeReference<T>>
    }

}