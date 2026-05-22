package com.wolfyscript.customcrafting.core.recipe.data

import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

internal data class CraftingRecipeInputImpl(override val matrixData: CraftingMatrixData) : RecipeInput.CraftingRecipeInput

internal data class GrindingRecipeInputImpl(
    override val base: ScafallItemStack?,
    override val addition: ScafallItemStack?
) : RecipeInput.GrindingRecipeInput

internal data class MixingRecipeInputImpl(override val input: Collection<ScafallItemStack?>) : RecipeInput.MixingRecipeInput

internal data class RepairingRecipeInputImpl(
    override val base: ScafallItemStack,
    override val addition: ScafallItemStack?,
    override val itemName: String?
) : RecipeInput.RepairingRecipeInput

internal data class SmithingRecipeInputImpl(
    override val template: ScafallItemStack?,
    override val base: ScafallItemStack?,
    override val addition: ScafallItemStack?
) : RecipeInput.SmithingRecipeInput

internal data class SingleSlotRecipeInputImpl(override val source: ScafallItemStack) : RecipeInput.SingleSlotRecipeInput

