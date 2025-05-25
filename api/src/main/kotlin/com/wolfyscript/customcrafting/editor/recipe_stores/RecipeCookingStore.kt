package com.wolfyscript.customcrafting.editor.recipe_stores

import com.wolfyscript.customcrafting.editor.RecipeStore
import com.wolfyscript.customcrafting.editor.result.ResultStore
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking

interface RecipeCookingStore : RecipeStore.RecipeTypeSpecificStore<CustomRecipeCooking> {

    val result: ResultStore



}