package com.wolfyscript.customcrafting.factories

import com.wolfyscript.customcrafting.recipes.data.CookingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixDataImpl
import com.wolfyscript.customcrafting.recipes.data.CraftingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.GrindingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.MixingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.data.RepairingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.SmithingRecipeInputImpl
import com.wolfyscript.customcrafting.recipes.data.StonecuttingRecipeInputImpl
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class RecipeFactoryCommon : RecipeFactory {

    override fun createMatrixData(ingredients: List<ItemStack?>): CraftingMatrixData {
        return CraftingMatrixDataImpl(ingredients)
    }

    override fun createCookingRecipeInput(source: ItemStack, fuel: ItemStack?): RecipeInput.CookingRecipeInput {
        return CookingRecipeInputImpl(source, fuel)
    }

    override fun createCraftingRecipeInput(matrixData: CraftingMatrixData): RecipeInput.CraftingRecipeInput {
        return CraftingRecipeInputImpl(matrixData)
    }

    override fun createGrindingRecipeInput(
        topInput: ItemStack?,
        bottomInput: ItemStack?,
    ): RecipeInput.GrindingRecipeInput {
        return GrindingRecipeInputImpl(topInput, bottomInput)
    }

    override fun createMixingRecipeInput(input: Collection<ItemStack?>): RecipeInput.MixingRecipeInput {
        return MixingRecipeInputImpl(input)
    }

    override fun createRepairingRecipeInput(
        base: ItemStack?,
        addition: ItemStack?,
    ): RecipeInput.RepairingRecipeInput {
        return RepairingRecipeInputImpl(base, addition)
    }

    override fun createSmithingRecipeInput(
        template: ItemStack?,
        base: ItemStack?,
        addition: ItemStack?,
    ): RecipeInput.SmithingRecipeInput {
        return SmithingRecipeInputImpl(template, base, addition)
    }

    override fun createStonecuttingRecipeInput(source: ItemStack): RecipeInput.StonecuttingRecipeInput {
        return StonecuttingRecipeInputImpl(source)
    }
}