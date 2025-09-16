package com.wolfyscript.customcrafting.factories

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.customcrafting.recipes.RecipeReferenceImpl
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.CraftingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.GrindingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.MixingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.data.RepairingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.SmithingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.SingleSlotRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.toCraftingMatrixData
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

class RecipeFactoryCommon : RecipeFactory {

    override fun <T : CustomRecipe<*, *>> createRecipeReference(
        key: Key,
        recipe: T,
    ): RecipeReference<T> {
        return RecipeReferenceImpl(key, recipe)
    }

    override fun createMatrixData(ingredients: List<ScafallItemStack?>): CraftingMatrixData {
        return ingredients.toCraftingMatrixData()
    }

    override fun createCraftingRecipeInput(matrixData: CraftingMatrixData): RecipeInput.CraftingRecipeInput {
        return CraftingRecipeInputImpl(matrixData)
    }

    override fun createGrindingRecipeInput(
        base: ScafallItemStack?,
        addition: ScafallItemStack?,
    ): RecipeInput.GrindingRecipeInput {
        return GrindingRecipeInputImpl(base, addition)
    }

    override fun createMixingRecipeInput(input: Collection<ScafallItemStack?>): RecipeInput.MixingRecipeInput {
        return MixingRecipeInputImpl(input)
    }

    override fun createRepairingRecipeInput(
        base: ScafallItemStack,
        addition: ScafallItemStack?,
        itemName: String?,
    ): RecipeInput.RepairingRecipeInput {
        return RepairingRecipeInputImpl(base, addition, itemName)
    }

    override fun createSmithingRecipeInput(
        template: ScafallItemStack?,
        base: ScafallItemStack?,
        addition: ScafallItemStack?,
    ): RecipeInput.SmithingRecipeInput {
        return SmithingRecipeInputImpl(template, base, addition)
    }

    override fun createSingleSlotRecipeInput(source: ScafallItemStack): RecipeInput.SingleSlotRecipeInput {
        return SingleSlotRecipeInputImpl(source)
    }
}