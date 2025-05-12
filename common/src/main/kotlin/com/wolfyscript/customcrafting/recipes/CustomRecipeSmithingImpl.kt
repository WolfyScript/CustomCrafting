package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class CustomRecipeSmithingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val template: Ingredient?,
    override val base: Ingredient?,
    override val addition: Ingredient?,
    override val copyOptions: CustomRecipeSmithing.CopyOptions?,
    override val result: RecipeResult,
) : CustomRecipeSmithing {

    override val type: RecipeType<CustomRecipeSmithing>
        get() = TODO("Not yet implemented")

    override fun evaluate(
        context: EvaluationContext,
        templateStack: ItemStack?,
        baseStack: ItemStack?,
        additionStack: ItemStack?,
    ): RecipeData<CustomRecipeSmithing>? {
        TODO("Not yet implemented")
    }

    data class CopyOptionsImpl(override val preserveComponents: List<Key>) : CustomRecipeSmithing.CopyOptions

}