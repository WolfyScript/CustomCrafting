package com.wolfyscript.customcrafting.factories

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface RecipeFactory {

    fun <T: CustomRecipe<*,*>> createRecipeReference(key: Key, recipe: T): RecipeReference<T>

    fun createMatrixData(ingredients: List<ItemStack?>) : CraftingMatrixData

    fun createCraftingRecipeInput(matrixData: CraftingMatrixData) : RecipeInput.CraftingRecipeInput

    fun createGrindingRecipeInput(base: ItemStack?, addition: ItemStack?) : RecipeInput.GrindingRecipeInput

    fun createMixingRecipeInput(input: Collection<ItemStack?>) : RecipeInput.MixingRecipeInput

    fun createRepairingRecipeInput(base: ItemStack, addition: ItemStack?, itemName: String?) : RecipeInput.RepairingRecipeInput

    fun createSmithingRecipeInput(template: ItemStack?, base: ItemStack?, addition: ItemStack?) : RecipeInput.SmithingRecipeInput

    fun createSingleSlotRecipeInput(source: ItemStack) : RecipeInput.SingleSlotRecipeInput

}