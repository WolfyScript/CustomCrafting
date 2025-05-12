package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class CustomRecipeGrindingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val xp: Int = 0,
    override val ingredients: List<Ingredient>,
    override val result: RecipeResult,
) : CustomRecipeGrinding {

    override val type: RecipeType<CustomRecipeGrinding>
        get() = TODO("Not yet implemented")

    override fun evaluate(
        context: EvaluationContext,
        topStack: ItemStack?,
        bottomStack: ItemStack?,
    ): RecipeData<CustomRecipeGrinding>? {
        TODO("Not yet implemented")
    }

}