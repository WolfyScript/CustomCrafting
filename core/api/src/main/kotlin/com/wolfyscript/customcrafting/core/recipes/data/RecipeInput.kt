package com.wolfyscript.customcrafting.core.recipes.data

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

/**
 * Contains the input a recipe requires to be evaluated.
 */
interface RecipeInput {

    interface CraftingRecipeInput : RecipeInput {

        val matrixData: CraftingMatrixData

        companion object {

            fun of(matrixData: CraftingMatrixData) = CustomCraftingProvider.get().factories.recipeFactory.createCraftingRecipeInput(matrixData)

        }

    }

    interface SmithingRecipeInput : RecipeInput {

        val template: ScafallItemStack?

        val base: ScafallItemStack?

        val addition: ScafallItemStack?

        companion object {

            fun of(template: ScafallItemStack?, base: ScafallItemStack?, addition: ScafallItemStack?) = CustomCraftingProvider.get().factories.recipeFactory.createSmithingRecipeInput(template, base, addition)

        }

    }

    interface MixingRecipeInput : RecipeInput {

        val input: Collection<ScafallItemStack?>

        companion object {

            fun of(input: Collection<ScafallItemStack?>) = CustomCraftingProvider.get().factories.recipeFactory.createMixingRecipeInput(input)

        }

    }

    interface GrindingRecipeInput : RecipeInput {

        val base: ScafallItemStack?

        val addition: ScafallItemStack?

        companion object {

            fun of(base: ScafallItemStack?, addition: ScafallItemStack?) = CustomCraftingProvider.get().factories.recipeFactory.createGrindingRecipeInput(base, addition)

        }

    }

    interface SingleSlotRecipeInput : RecipeInput {

        val source: ScafallItemStack

        companion object {

            fun of(source: ScafallItemStack) = CustomCraftingProvider.get().factories.recipeFactory.createSingleSlotRecipeInput(source)

        }

    }

    interface RepairingRecipeInput : RecipeInput {

        val itemName: String?

        val base: ScafallItemStack

        val addition: ScafallItemStack?

        companion object {

            fun of(base: ScafallItemStack, addition: ScafallItemStack?, itemName: String?) = CustomCraftingProvider.get().factories.recipeFactory.createRepairingRecipeInput(
                base,
                addition,
                itemName
            )

        }

    }

}