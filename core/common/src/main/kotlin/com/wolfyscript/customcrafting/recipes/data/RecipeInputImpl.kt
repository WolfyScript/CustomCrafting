package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

data class CraftingRecipeInputImpl(override val matrixData: CraftingMatrixData) : RecipeInput.CraftingRecipeInput

data class GrindingRecipeInputImpl(
    override val base: ScafallItemStack?,
    override val addition: ScafallItemStack?
) : RecipeInput.GrindingRecipeInput

data class MixingRecipeInputImpl(override val input: Collection<ScafallItemStack?>) : RecipeInput.MixingRecipeInput

data class RepairingRecipeInputImpl(
    override val base: ScafallItemStack,
    override val addition: ScafallItemStack?,
    override val itemName: String?
) : RecipeInput.RepairingRecipeInput

data class SmithingRecipeInputImpl(
    override val template: ScafallItemStack?,
    override val base: ScafallItemStack?,
    override val addition: ScafallItemStack?
) : RecipeInput.SmithingRecipeInput

data class SingleSlotRecipeInputImpl(override val source: ScafallItemStack) : RecipeInput.SingleSlotRecipeInput

