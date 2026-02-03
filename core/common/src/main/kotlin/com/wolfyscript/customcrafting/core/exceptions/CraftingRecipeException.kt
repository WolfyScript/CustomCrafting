package com.wolfyscript.customcrafting.core.exceptions

import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.RecipeReference

class CraftingRecipeException(
    message: String,
    cause: Throwable,
    recipe: RecipeReference<CustomRecipeCrafting>? = null,
) : Exception(message, cause)