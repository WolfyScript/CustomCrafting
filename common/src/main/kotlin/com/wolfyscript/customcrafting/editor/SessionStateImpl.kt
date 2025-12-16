package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.resource.DataType
import com.wolfyscript.scafall.identifier.Key

private fun saveRecipe(key: Key, recipe: CustomRecipe<*,*>) {
    val resourceLoader = CustomCraftingProvider.get().server?.resourceManager?.resourceLoader ?: return
    resourceLoader.save(DataType.Recipes, key, recipe)
}

class EditRecipeSessionState(key: Key, recipeState: RecipeStore<*>) : SessionState.EditState {

    override var currentKey: Key = key
        private set
    override val recipeStore: RecipeStore<*> = recipeState

    override fun saveAs(key: Key) {
        currentKey = key
        save()
    }

    override fun save() {
        val result = recipeStore.complete()
        if (result.isFailure) {
            return
        }
        val recipe = result.getOrThrow()
        saveRecipe(currentKey, recipe)
        // TODO: update recipe manager? or require to manually reload later?
    }

    override fun cancel() {


    }

}

class CreateRecipeSessionState(override val recipeStore: RecipeStore<*>) : SessionState.CreateState {

    override fun save(key: Key) {
        val result = recipeStore.complete()
        if (result.isFailure) {
            return
        }
        val recipe = result.getOrThrow()
        saveRecipe(key, recipe)
    }

    override fun cancel() {

    }

}