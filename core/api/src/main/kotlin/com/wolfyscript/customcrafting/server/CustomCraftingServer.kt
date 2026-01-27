package com.wolfyscript.customcrafting.server

import com.wolfyscript.customcrafting.recipes.RecipeManager
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientManager
import com.wolfyscript.customcrafting.resource.ResourceManager
import com.wolfyscript.scafall.loader.module.Server

/**
 * The CustomCrafting API that is only available on the server (Integrated or Dedicated server)
 */
interface CustomCraftingServer : Server {

    val recipeManager: RecipeManager

    val ingredientManager: IngredientManager

    val resourceManager: ResourceManager

}