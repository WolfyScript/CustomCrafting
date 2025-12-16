package com.wolfyscript.customcrafting.editor.recipes

import com.wolfyscript.customcrafting.editor.RecipeState
import com.wolfyscript.customcrafting.editor.conditions.RecipeConditionsStore
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeType

class RecipeStateImpl<T: CustomRecipe<*, *>>(
    override val recipeType: RecipeType<T>,
    override val recipeTypeSpecificState: RecipeState.RecipeTypeSpecificState<T>
) : RecipeState<T> {

    override var priority: Int = 0
    override var condition: RecipeConditionsStore? = null

    override fun complete(): Result<T> {
        return recipeTypeSpecificState.complete(this)
    }
}