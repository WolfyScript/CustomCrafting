package com.wolfyscript.customcrafting.core.exceptions

import com.wolfyscript.customcrafting.core.recipe.CustomRecipeCrafting
import com.wolfyscript.customcrafting.core.recipe.RecipeReference

/**
 * Exception class for errors related to crafting recipes.
 *
 * @param message The detail message.
 * @param cause The cause of this exception.
 * @param recipe The reference to the custom crafting recipe that caused the exception, if applicable.
 */
class CraftingRecipeException(
    message: String,
    cause: Throwable,
    recipe: RecipeReference<CustomRecipeCrafting>? = null,
) : Exception(message, cause)