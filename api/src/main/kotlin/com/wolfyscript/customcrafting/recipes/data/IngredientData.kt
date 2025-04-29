package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.customcrafting.recipes.Ingredient

interface IngredientData {

    val invSlot: Int

    val recipeIndex: Int

    val selectedIngredient: Ingredient


}