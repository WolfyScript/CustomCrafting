package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface CustomRecipeGrinding : CustomRecipe {

    override val type: RecipeType<CustomRecipeGrinding>
        get() = RecipeTypes.grinding

    /**
     * A list of ingredients to be used in the Grindstone.
     * Limited to 2 ingredients, the rest is ignored.
     */
    val ingredients: List<Ingredient>

    val result: RecipeResult

    val xp: Int

    fun evaluate(context: EvaluationContext, topStack: ItemStack?, bottomStack: ItemStack?): RecipeData<CustomRecipeGrinding>?

}