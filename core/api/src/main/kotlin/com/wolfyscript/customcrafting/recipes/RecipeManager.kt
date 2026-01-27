package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.resource.LoadedObject
import com.wolfyscript.customcrafting.resource.ResourceListener
import com.wolfyscript.scafall.identifier.Key

/**
 * Keeps track of all the recipes in CustomCrafting and provides methods to evaluate them.
 *
 * The [RecipeManager] is responsible to make sure recipes are properly enabled/disabled, removed, updated, and evaluated.
 *
 * It is not responsible for crawling and loading recipe files!
 * It accepts loaded recipes from the [ResourceLoader][com.wolfyscript.customcrafting.resource.ResourceLoader].
 *
 */
interface RecipeManager : ResourceListener {

    companion object {
        const val LOG_PREFIX = "[Recipe Manager] "
    }

    fun <I: RecipeInput, D: RecipeEvaluationResult.Data, T: CustomRecipe<I,D>> evaluateRecipesOfType(type: RecipeType<T>, input: I, context: EvaluationContext): RecipeEvaluationResult<D,T>?

    fun disableRecipe(recipe: Key)

    fun enableRecipe(key: Key)

    fun isRecipeDisabled(key: Key): Boolean

    val disabledRecipes: Set<Key>

    /**
     * Gets a [RecipeReference] to the recipe with the given [key].
     *
     * While the reference can be cached, the recipe value of the reference should not be cached separately!
     */
    fun getRecipe(key: Key): RecipeReference<*>?

    /**
     * Registers new recipes or updates existing recipes in the [RecipeManager] and re-indexes them by type and key.
     */
    fun registerOrUpdateRecipes(recipes: Collection<LoadedObject<CustomRecipe<*,*>>>)

    /**
     * Removes the recipes from the [RecipeManager] and re-indexes the recipes by type and key.
     */
    fun removeRecipes(vararg recipes: Key)

    /**
     * Gets all the recipe references in the [RecipeManager].
     */
    fun recipes(): Collection<RecipeReference<*>>

}

inline fun <reified T : CustomRecipe<*, *>> RecipeManager.getRecipeTyped(key: Key, type: RecipeType<T>): RecipeReference<T>? {
    val ref = getRecipe(key) ?: return null
    if (ref.type != type) {
        return null
    }
    return try {
        ref as? RecipeReference<T>
    } catch (_: ClassCastException) {
        null
    }
}