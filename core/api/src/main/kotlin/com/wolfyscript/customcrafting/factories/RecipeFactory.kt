package com.wolfyscript.customcrafting.factories

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

/**
 * Factory functions related to custom recipes.
 *
 * When using Kotlin these functions are usually available via convenient extension or top-level functions.
 */
interface RecipeFactory {

    /**
     * Creates a RecipeReference from the specified [key] and [recipe]
     *
     * @see RecipeReference.of
     */
    fun <T : CustomRecipe<*, *>> createRecipeReference(key: Key, recipe: T): RecipeReference<T>

    /**
     * @see CraftingMatrixData.of
     */
    fun createMatrixData(ingredients: List<ScafallItemStack?>) : CraftingMatrixData

    /**
     * @see RecipeInput.CraftingRecipeInput.of
     */
    fun createCraftingRecipeInput(matrixData: CraftingMatrixData) : RecipeInput.CraftingRecipeInput

    /**
     * @see RecipeInput.GrindingRecipeInput.of
     */
    fun createGrindingRecipeInput(base: ScafallItemStack?, addition: ScafallItemStack?) : RecipeInput.GrindingRecipeInput

    /**
     * @see RecipeInput.MixingRecipeInput.of
     */
    fun createMixingRecipeInput(input: Collection<ScafallItemStack?>) : RecipeInput.MixingRecipeInput

    /**
     * @see RecipeInput.RepairingRecipeInput.of
     */
    fun createRepairingRecipeInput(base: ScafallItemStack, addition: ScafallItemStack?, itemName: String?) : RecipeInput.RepairingRecipeInput

    /**
     * @see RecipeInput.SmithingRecipeInput.of
     */
    fun createSmithingRecipeInput(template: ScafallItemStack?, base: ScafallItemStack?, addition: ScafallItemStack?) : RecipeInput.SmithingRecipeInput

    /**
     * @see RecipeInput.SingleSlotRecipeInput.of
     */
    fun createSingleSlotRecipeInput(source: ScafallItemStack) : RecipeInput.SingleSlotRecipeInput

}