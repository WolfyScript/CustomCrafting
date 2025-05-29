package com.wolfyscript.customcrafting.factories

import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface RecipeFactory {

    fun createMatrixData(ingredients: List<ItemStack?>) : CraftingMatrixData

}