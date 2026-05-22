package com.wolfyscript.customcrafting.core.server

import com.wolfyscript.customcrafting.core.recipe.RecipeManager
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientManager
import com.wolfyscript.customcrafting.core.resource.ResourceManager
import com.wolfyscript.scafall.loader.module.Server

/**
 * The CustomCrafting API that is only available on the server (Integrated or Dedicated server)
 */
interface CustomCraftingServer : Server {

    val recipeManager: RecipeManager

    val ingredientManager: IngredientManager

    val resourceManager: ResourceManager

}