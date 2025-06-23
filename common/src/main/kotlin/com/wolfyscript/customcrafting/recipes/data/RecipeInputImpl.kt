package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.scafall.wrappers.world.items.ItemStack

data class CookingRecipeInputImpl(
    override val source: ItemStack,
    override val fuel: ItemStack?
) : RecipeInput.CookingRecipeInput

data class CraftingRecipeInputImpl(override val matrixData: CraftingMatrixData) : RecipeInput.CraftingRecipeInput

data class GrindingRecipeInputImpl(
    override val topInput: ItemStack?,
    override val bottomInput: ItemStack?
) : RecipeInput.GrindingRecipeInput

data class MixingRecipeInputImpl(override val input: Collection<ItemStack?>) : RecipeInput.MixingRecipeInput

data class RepairingRecipeInputImpl(
    override val base: ItemStack,
    override val addition: ItemStack?,
    override val itemName: String?
) : RecipeInput.RepairingRecipeInput

data class SmithingRecipeInputImpl(
    override val template: ItemStack?,
    override val base: ItemStack?,
    override val addition: ItemStack?
) : RecipeInput.SmithingRecipeInput

data class StonecuttingRecipeInputImpl(override val source: ItemStack) : RecipeInput.StonecuttingRecipeInput

