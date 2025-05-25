package com.wolfyscript.customcrafting.editor.recipe_stores

import com.wolfyscript.customcrafting.editor.IngredientStore
import com.wolfyscript.customcrafting.editor.RecipeStore
import com.wolfyscript.customcrafting.editor.result.ResultStore
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting

interface RecipeCraftingStore : RecipeStore.RecipeTypeSpecificStore<CustomRecipeCrafting> {

    val result: ResultStore

    val formula: CraftingFormulaStore

    interface CraftingFormulaStore {

        interface Shapeless : CraftingFormulaStore {

            val ingredients: MutableList<IngredientStore>

        }

        interface Shaped : CraftingFormulaStore {

            val ingredients: MutableList<IngredientStore>

            var shape: ShapeStore

            interface ShapeStore {

                var symmetry: CraftingFormula.Shaped.ShapeSymmetry

                var trim: Boolean

            }

        }

    }

}