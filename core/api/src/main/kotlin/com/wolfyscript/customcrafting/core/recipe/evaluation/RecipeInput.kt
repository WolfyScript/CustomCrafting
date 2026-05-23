package com.wolfyscript.customcrafting.core.recipe.evaluation

import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

/**
 * Contains the input a recipe requires to be evaluated.
 */
interface RecipeInput {

    interface CraftingRecipeInput : RecipeInput {

        val matrixData: CraftingMatrixData

        companion object {

            fun of(matrixData: CraftingMatrixData): CraftingRecipeInput {
                return CraftingRecipeInputImpl(matrixData)
            }

        }

    }

    interface SmithingRecipeInput : RecipeInput {

        val template: ScafallItemStack?

        val base: ScafallItemStack?

        val addition: ScafallItemStack?

        companion object {

            fun of(template: ScafallItemStack?, base: ScafallItemStack?, addition: ScafallItemStack?): SmithingRecipeInput {
                return SmithingRecipeInputImpl(template, base, addition)
            }

        }

    }

    interface MixingRecipeInput : RecipeInput {

        val input: Collection<ScafallItemStack?>

        companion object {

            fun of(input: Collection<ScafallItemStack?>): MixingRecipeInput {
                return MixingRecipeInputImpl(input)
            }

        }

    }

    interface GrindingRecipeInput : RecipeInput {

        val base: ScafallItemStack?

        val addition: ScafallItemStack?

        companion object {

            fun of(base: ScafallItemStack?, addition: ScafallItemStack?): GrindingRecipeInput {
                return GrindingRecipeInputImpl(base, addition)
            }

        }

    }

    interface SingleSlotRecipeInput : RecipeInput {

        val source: ScafallItemStack

        companion object {

            fun of(source: ScafallItemStack) : SingleSlotRecipeInput {
                return SingleSlotRecipeInputImpl(source)
            }

        }

    }

    interface RepairingRecipeInput : RecipeInput {

        val itemName: String?

        val base: ScafallItemStack

        val addition: ScafallItemStack?

        companion object {

            fun of(base: ScafallItemStack, addition: ScafallItemStack?, itemName: String?): RepairingRecipeInput {
                return RepairingRecipeInputImpl(
                    base,
                    addition,
                    itemName
                )
            }

        }

    }

}