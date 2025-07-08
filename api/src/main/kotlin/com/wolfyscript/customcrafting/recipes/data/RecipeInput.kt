package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

/**
 * Contains the input a recipe requires to be evaluated.
 */
interface RecipeInput {

    /**
     * The input for all the cooking recipes
     */
    interface CookingRecipeInput : RecipeInput {

        val source: ItemStack

        val fuel: ItemStack?

        companion object {

            fun of(source: ItemStack, fuel: ItemStack?) = CustomCraftingProvider.get().factories.recipeFactory.createCookingRecipeInput(source, fuel)

        }

    }

    interface CraftingRecipeInput : RecipeInput {

        val matrixData: CraftingMatrixData

        companion object {

            fun of(matrixData: CraftingMatrixData) = CustomCraftingProvider.get().factories.recipeFactory.createCraftingRecipeInput(matrixData)

        }

    }

    interface SmithingRecipeInput : RecipeInput {

        val template: ItemStack?

        val base: ItemStack?

        val addition: ItemStack?

        companion object {

            fun of(template: ItemStack?, base: ItemStack?, addition: ItemStack?) = CustomCraftingProvider.get().factories.recipeFactory.createSmithingRecipeInput(template, base, addition)

        }

    }

    interface MixingRecipeInput : RecipeInput {

        val input: Collection<ItemStack?>

        companion object {

            fun of(input: Collection<ItemStack?>) = CustomCraftingProvider.get().factories.recipeFactory.createMixingRecipeInput(input)

        }

    }

    interface GrindingRecipeInput : RecipeInput {

        val base: ItemStack?

        val addition: ItemStack?

        companion object {

            fun of(base: ItemStack?, addition: ItemStack?) = CustomCraftingProvider.get().factories.recipeFactory.createGrindingRecipeInput(base, addition)

        }

    }

    interface StonecuttingRecipeInput : RecipeInput {

        val source: ItemStack

        companion object {

            fun of(source: ItemStack) = CustomCraftingProvider.get().factories.recipeFactory.createStonecuttingRecipeInput(source)

        }

    }

    interface RepairingRecipeInput : RecipeInput {

        val itemName: String?

        val base: ItemStack

        val addition: ItemStack?

        companion object {

            fun of(base: ItemStack, addition: ItemStack?, itemName: String?) = CustomCraftingProvider.get().factories.recipeFactory.createRepairingRecipeInput(
                base,
                addition,
                itemName
            )

        }

    }

}