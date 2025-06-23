package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing
import com.wolfyscript.customcrafting.recipes.RecipeResult

open class RecipeDataImpl<T: CustomRecipe<*,*>>(
    override val recipe: T,
    val ingredients: Array<IngredientData?>
) : RecipeData<T> {

    override val nonNullIngredients: List<IngredientData> by lazy { ingredients.filterNotNull() }

    override fun bySlot(slot: Int): IngredientData? {
        if (slot < 0 || slot >= ingredients.size) return null
        return ingredients[slot]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecipeDataImpl<*>) return false

        if (recipe != other.recipe) return false
        if (!ingredients.contentEquals(other.ingredients)) return false
        if (nonNullIngredients != other.nonNullIngredients) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = recipe.hashCode()
        result1 = 31 * result1 + ingredients.contentHashCode()
        result1 = 31 * result1 + nonNullIngredients.hashCode()
        return result1
    }
}

class RepairingRecipeDataImpl(
    override var itemRepairCost: Int?,
    recipe: CustomRecipeRepairing,
    ingredients: Array<IngredientData?>
) : RecipeDataImpl<CustomRecipeRepairing>(recipe, ingredients), RecipeData.RepairingRecipeData
