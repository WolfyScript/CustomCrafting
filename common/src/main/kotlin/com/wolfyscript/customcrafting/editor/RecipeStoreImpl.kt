package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.editor.conditions.RecipeConditionsStore
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeType

class RecipeStoreImpl<T: CustomRecipe<*,*>>(
    override val recipeType: RecipeType<T>,
    override val recipeTypeSpecificStore: RecipeStore.RecipeTypeSpecificStore<T>
) : RecipeStore<T> {

    override var priority: Int = 0
    override var condition: RecipeConditionsStore? = null

    override fun complete(): Result<T> {
        return recipeTypeSpecificStore.complete(this)
    }
}