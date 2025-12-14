package com.wolfyscript.customcrafting.configuration.editor

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.EditorSession
import com.wolfyscript.customcrafting.editor.RecipeStore
import com.wolfyscript.customcrafting.editor.SessionState
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.identifier.Key
import java.util.*

class EditorSessionImpl(
    override val user: UUID,
) : EditorSession {

    override var state: SessionState? = null
        private set

    override fun edit(recipeKey: Key): Result<SessionState> {
        val recipe = CustomCraftingProvider.get().server?.recipeManager?.getRecipe(recipeKey)?.value
            ?: return Result.failure(IllegalArgumentException("Recipe $recipeKey not found"))
        val store = edit(recipe)
            ?: return Result.failure(IllegalArgumentException("Failed to load recipe $recipeKey: missing type factory for editor. Is it registered?"))
        state = EditRecipeSessionState(recipeKey, store)
        return Result.success(state!!)
    }

    override fun create(recipeType: RecipeType<*>): Result<SessionState> {
        val store = createTyped(recipeType)
            ?: return Result.failure(IllegalArgumentException("Failed to create editor store: missing type factory for recipe type $recipeType"))
        state = CreateRecipeSessionState(store)
        return Result.success(state!!)
    }

    private fun <T: CustomRecipe<*,*>> createTyped(recipeType: RecipeType<T>): RecipeStore<T>? {
        val storeTypes = CustomCraftingRegistryTypes.recipeTypeSpecificStoreFactories.resolveOrThrow()
        return storeTypes.values().firstOrNull { it.recipeType == recipeType }?.let {
            it as RecipeStore.RecipeTypeSpecificStore.Factory<T>
            RecipeStoreImpl(recipeType, it.create())
        }
    }

    private fun <T : CustomRecipe<*, *>> edit(recipe: T): RecipeStore<T>? {
        val storeTypes = CustomCraftingRegistryTypes.recipeTypeSpecificStoreFactories.resolveOrThrow()
        val store = storeTypes.values().firstOrNull { it.recipeType == recipe.type }?.let {
            it as RecipeStore.RecipeTypeSpecificStore.Factory<T>
            it.edit(recipe)
        } ?: return null
        return RecipeStoreImpl(recipe.type as RecipeType<T>, store)
    }

    override fun cancel() {
        // TODO: Reset state
        state = null
    }

}