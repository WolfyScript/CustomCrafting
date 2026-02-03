package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.editor.domain.model.recipe.conditions.RecipeConditionsModel
import com.wolfyscript.customcrafting.core.recipes.CustomRecipe
import com.wolfyscript.customcrafting.core.recipes.RecipeType

internal class RecipeModelImpl<T: CustomRecipe<*, *>>(
    override val recipeType: RecipeType<T>,
    override val recipeTypeSpecificModel: RecipeModel.RecipeTypeSpecificModel<T>
) : RecipeModel<T> {

    override var priority: Int = 0
    override var condition: RecipeConditionsModel? = null

    override fun complete(): Result<T> {
        return recipeTypeSpecificModel.complete(this)
    }
}