package com.wolfyscript.customcrafting.recipes

class CustomRecipeMixingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions
) : CustomRecipeMixing {

    override val type: RecipeType<CustomRecipeMixing>
        get() = TODO("Not yet implemented")

}