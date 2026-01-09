package com.wolfyscript.customcrafting.editor.domain

import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeModel
import com.wolfyscript.customcrafting.editor.domain.recipes.conditions.RecipeConditionsModel
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeType

class RecipeModelImpl<T: CustomRecipe<*, *>>(
    override val recipeType: RecipeType<T>,
    override val recipeTypeSpecificModel: RecipeModel.RecipeTypeSpecificModel<T>
) : RecipeModel<T> {

    override var priority: Int = 0
    override var condition: RecipeConditionsModel? = null

    override fun complete(): Result<T> {
        return recipeTypeSpecificModel.complete(this)
    }
}