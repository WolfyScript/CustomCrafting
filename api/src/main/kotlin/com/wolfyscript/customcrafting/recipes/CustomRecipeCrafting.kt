package com.wolfyscript.customcrafting.recipes

interface CustomRecipeCrafting : CustomRecipe<CustomRecipeCrafting> {

    val formula: CraftingFormula

    val result: RecipeResult

}

interface CraftingFormula {

    val ingredients: List<Ingredient>

    interface Shapeless : CraftingFormula {

    }

    interface Shaped : CraftingFormula {

        val shape: Shape

        val symmetry: ShapeSymmetry

        interface ShapeSymmetry {

        }

        interface Shape {

        }
        
    }
    
}
