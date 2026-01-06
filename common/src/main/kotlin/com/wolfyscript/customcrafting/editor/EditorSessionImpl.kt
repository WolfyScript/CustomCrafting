package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.model.CreateRecipeSessionModel
import com.wolfyscript.customcrafting.editor.model.EditRecipeSessionModel
import com.wolfyscript.customcrafting.editor.model.SessionModel
import com.wolfyscript.customcrafting.editor.model.recipes.RecipeModel
import com.wolfyscript.customcrafting.editor.model.recipes.RecipeModelImpl
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.identifier.Key
import java.util.*

class EditorSessionImpl(
    override val user: UUID,
) : EditorSession {

    override var state: SessionModel? = null
        private set

    override fun edit(recipeKey: Key): Result<SessionModel> {
        val recipe = CustomCraftingProvider.get().server?.recipeManager?.getRecipe(recipeKey)?.value
            ?: return Result.failure(IllegalArgumentException("Recipe $recipeKey not found"))
        val store = edit(recipe)
            ?: return Result.failure(IllegalArgumentException("Failed to load recipe $recipeKey: missing type factory for editor. Is it registered?"))
        state = EditRecipeSessionModel(recipeKey, store)
        return Result.success(state!!)
    }

    override fun create(recipeType: RecipeType<*>): Result<SessionModel> {
        if (state != null) {
            return Result.failure(IllegalStateException("Already editing a recipe of type ${state!!.recipeModel.recipeType}. Cancel and try again."))
        }

        val store = createTyped(recipeType)
            ?: return Result.failure(IllegalArgumentException("Failed to create editor store: missing type factory for recipe type $recipeType"))
        state = CreateRecipeSessionModel(store)
        return Result.success(state!!)
    }

    private fun <T: CustomRecipe<*,*>> createTyped(recipeType: RecipeType<T>): RecipeModel<T>? {
        val storeTypes = CustomCraftingRegistryTypes.recipeTypeSpecificModelFactories.resolveOrThrow()
        return storeTypes.values().firstOrNull { it.recipeType == recipeType }?.let {
            it as RecipeModel.RecipeTypeSpecificModel.Factory<T>
            RecipeModelImpl(recipeType, it.create())
        }
    }

    private fun <T : CustomRecipe<*, *>> edit(recipe: T): RecipeModel<T>? {
        val storeTypes = CustomCraftingRegistryTypes.recipeTypeSpecificModelFactories.resolveOrThrow()
        val store = storeTypes.values().firstOrNull { it.recipeType == recipe.type }?.let {
            it as RecipeModel.RecipeTypeSpecificModel.Factory<T>
            it.edit(recipe)
        } ?: return null
        return RecipeModelImpl(recipe.type as RecipeType<T>, store)
    }

    override fun cancel() {
        // TODO: Reset state
        state = null
    }

}