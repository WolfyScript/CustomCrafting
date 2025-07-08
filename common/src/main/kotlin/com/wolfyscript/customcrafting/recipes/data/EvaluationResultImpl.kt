package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeReference

class RecipeEvaluationResultImpl<D: RecipeEvaluationResult.Data, T: CustomRecipe<*,*>>(
    override val recipe: RecipeReference<T>,
    override val data: D
) : RecipeEvaluationResult<D, T> {

    override fun hashCode(): Int {
        var result1 = recipe.hashCode()
        result1 = 31 * result1 + data.hashCode()
        return result1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecipeEvaluationResultImpl<*, *>) return false

        if (recipe != other.recipe) return false
        if (data != other.data) return false

        return true
    }
}

open class DefaultDataImpl(val ingredients: Array<IngredientData?>) : RecipeEvaluationResult.Data {

    override val nonNullIngredients: List<IngredientData> by lazy { ingredients.filterNotNull() }

    override fun bySlot(slot: Int): IngredientData? {
        if (slot < 0 || slot >= ingredients.size) return null
        return ingredients[slot]
    }

}

class RepairingRecipeDataImpl(
    override var itemRepairCost: Int?,
    ingredients: Array<IngredientData?>
) : DefaultDataImpl(ingredients), RecipeEvaluationResult.RepairingRecipeData

class GrindingRecipeDataImpl(
    ingredients: Array<IngredientData?>,
    override var penalty: Int = 0,
    override var yield: Int = 0,
) : DefaultDataImpl(ingredients), RecipeEvaluationResult.GrindingRecipeData