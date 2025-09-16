package com.wolfyscript.customcrafting.factories

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

interface RecipeFactory {

    fun <T: CustomRecipe<*,*>> createRecipeReference(key: Key, recipe: T): RecipeReference<T>

    fun createMatrixData(ingredients: List<ScafallItemStack?>) : CraftingMatrixData

    fun createCraftingRecipeInput(matrixData: CraftingMatrixData) : RecipeInput.CraftingRecipeInput

    fun createGrindingRecipeInput(base: ScafallItemStack?, addition: ScafallItemStack?) : RecipeInput.GrindingRecipeInput

    fun createMixingRecipeInput(input: Collection<ScafallItemStack?>) : RecipeInput.MixingRecipeInput

    fun createRepairingRecipeInput(base: ScafallItemStack, addition: ScafallItemStack?, itemName: String?) : RecipeInput.RepairingRecipeInput

    fun createSmithingRecipeInput(template: ScafallItemStack?, base: ScafallItemStack?, addition: ScafallItemStack?) : RecipeInput.SmithingRecipeInput

    fun createSingleSlotRecipeInput(source: ScafallItemStack) : RecipeInput.SingleSlotRecipeInput

}