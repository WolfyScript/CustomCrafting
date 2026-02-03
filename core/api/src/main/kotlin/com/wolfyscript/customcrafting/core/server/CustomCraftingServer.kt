package com.wolfyscript.customcrafting.core.server

import com.wolfyscript.customcrafting.core.recipes.RecipeManager
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientManager
import com.wolfyscript.customcrafting.core.resource.ResourceManager
import com.wolfyscript.scafall.loader.module.Server

/**
 * The CustomCrafting API that is only available on the server (Integrated or Dedicated server)
 */
interface CustomCraftingServer : Server {

    val recipeManager: com.wolfyscript.customcrafting.core.recipes.RecipeManager

    val ingredientManager: com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientManager

    val resourceManager: com.wolfyscript.customcrafting.core.resource.ResourceManager

}