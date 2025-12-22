package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.editor.model.recipes.RecipeState
import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing

interface RecipeRepairingState : RecipeState.RecipeTypeSpecificState<CustomRecipeRepairing> {
}