package com.wolfyscript.customcrafting.core.exceptions

import com.wolfyscript.customcrafting.core.recipe.CustomRecipeCrafting
import com.wolfyscript.customcrafting.core.recipe.RecipeReference

class CraftingRecipeException(
    message: String,
    cause: Throwable,
    recipe: RecipeReference<CustomRecipeCrafting>? = null,
) : Exception(message, cause)