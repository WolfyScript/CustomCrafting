package com.wolfyscript.customcrafting.fabric.inject

import com.wolfyscript.customcrafting.core.recipe.RecipeReference

/**
 * A proxy recipe delegates the logic of a vanilla recipe to the logic of the CustomCrafting recipe.
 * They allow CC to integrate with the vanilla recipe system and provide better compatibility.
 */
interface ProxyRecipe {

    val customRecipe: RecipeReference<*>

}