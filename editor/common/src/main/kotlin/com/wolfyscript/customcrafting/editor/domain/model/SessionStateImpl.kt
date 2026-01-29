package com.wolfyscript.customcrafting.editor.domain.model

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.SessionModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeModel
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.resource.DataType
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key

private fun saveRecipe(key: Key, recipe: CustomRecipe<*,*>) {
    val resourceLoader = CustomCraftingProvider.get().server?.resourceManager?.resourceLoader ?: return
    resourceLoader.save(DataType.Recipes, key, recipe)
    ScafallProvider.get().logger.info("[RecipeManager] Saved $key with $recipe")
}

internal class EditRecipeSessionModel(key: Key, override val recipeModel: RecipeModel<*>) : SessionModel.EditModel {

    override var currentKey: Key = key
        private set

    override fun saveAs(key: Key) {
        val result = recipeModel.complete()
        if (result.isFailure) {
            ScafallProvider.get().logger.error("Failed to save recipe: ", result.exceptionOrNull())
            return
        }
        val recipe = result.getOrThrow()
        currentKey = key
        saveRecipe(key, recipe)
        // TODO: update recipe manager? or require to manually reload later?
    }

    override fun save() {
        saveAs(currentKey)
    }

    override fun cancel() {


    }

}

internal class CreateRecipeSessionModel(override val recipeModel: RecipeModel<*>) : SessionModel.CreateModel {

    override fun save(key: Key) {
        val result = recipeModel.complete()
        if (result.isFailure) {
            ScafallProvider.get().logger.error("Failed to save recipe: ", result.exceptionOrNull())
            return
        }
        val recipe = result.getOrThrow()
        saveRecipe(key, recipe)
    }

    override fun cancel() {

    }

}