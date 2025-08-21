package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.scafall.wrappers.world.items.ItemStack

data class CraftingRecipeInputImpl(override val matrixData: CraftingMatrixData) : RecipeInput.CraftingRecipeInput

data class GrindingRecipeInputImpl(
    override val base: ItemStack?,
    override val addition: ItemStack?
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

data class SingleSlotRecipeInputImpl(override val source: ItemStack) : RecipeInput.SingleSlotRecipeInput

