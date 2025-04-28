package com.wolfyscript.customcrafting.recipes

interface CustomRecipeCrafting : CustomRecipe<CustomRecipeCrafting> {

    val formula: Formula

    interface Formula {

        val ingredients: List<Ingredient>

        interface Shapeless : Formula {

        }

        interface Shaped : Formula {

            val shape: CraftingShape

            val symmetry: CraftingShapeSymmetry

        }

    }

}

interface CraftingShapeSymmetry {

}

interface CraftingShape {

}