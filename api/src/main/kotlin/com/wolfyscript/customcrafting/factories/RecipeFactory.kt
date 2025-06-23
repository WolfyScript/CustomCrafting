package com.wolfyscript.customcrafting.factories

import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface RecipeFactory {

    fun createMatrixData(ingredients: List<ItemStack?>) : CraftingMatrixData

    fun createCookingRecipeInput(source: ItemStack, fuel: ItemStack?) : RecipeInput.CookingRecipeInput

    fun createCraftingRecipeInput(matrixData: CraftingMatrixData) : RecipeInput.CraftingRecipeInput

    fun createGrindingRecipeInput(topInput: ItemStack?, bottomInput: ItemStack?) : RecipeInput.GrindingRecipeInput

    fun createMixingRecipeInput(input: Collection<ItemStack?>) : RecipeInput.MixingRecipeInput

    fun createRepairingRecipeInput(base: ItemStack, addition: ItemStack?, itemName: String?) : RecipeInput.RepairingRecipeInput

    fun createSmithingRecipeInput(template: ItemStack?, base: ItemStack?, addition: ItemStack?) : RecipeInput.SmithingRecipeInput

    fun createStonecuttingRecipeInput(source: ItemStack) : RecipeInput.StonecuttingRecipeInput

}