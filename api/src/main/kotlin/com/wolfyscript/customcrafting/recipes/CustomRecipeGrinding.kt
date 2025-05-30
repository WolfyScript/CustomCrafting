package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeInput

interface CustomRecipeGrinding : CustomRecipe<RecipeInput.GrindingRecipeInput, CustomRecipeGrinding> {

    override val type: RecipeType<CustomRecipeGrinding>
        get() = RecipeTypes.grinding

    /**
     * A list of ingredients to be used in the Grindstone.
     * Limited to 2 ingredients, the rest is ignored.
     */
    val ingredients: List<Ingredient>

    val result: RecipeResult

    val xp: Int

}