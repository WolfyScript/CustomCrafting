package com.wolfyscript.customcrafting.recipes

class CustomRecipeSmithingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions
) : CustomRecipeSmithing {

    override val type: RecipeType<CustomRecipeSmithing>
        get() = TODO("Not yet implemented")

}