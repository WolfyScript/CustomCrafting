package com.wolfyscript.customcrafting.editor.recipe_stores

import com.wolfyscript.customcrafting.editor.IngredientStore
import com.wolfyscript.customcrafting.editor.RecipeStore
import com.wolfyscript.customcrafting.editor.result.ResultStore
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient

interface RecipeCraftingStore : RecipeStore.RecipeTypeSpecificStore<CustomRecipeCrafting> {

    val result: ResultStore

    val formula: CraftingFormulaStore

    interface CraftingFormulaStore {

        interface Shapeless : CraftingFormulaStore {

            val ingredients: MutableList<IngredientStore>

            /**
             * Adds an ingredient to the end of the [ingredients]
             */
            fun addIngredient(ingredient: Ingredient)

            /**
             * Removes an ingredient from the [ingredients] at the specified index
             */
            fun removeIngredient(index: Int)

        }

        interface Shaped : CraftingFormulaStore {

            val ingredients: MutableList<IngredientStore>

            /**
             * Assigns an ingredient to the specified index in the recipe
             */
            fun assignIngredient(index: Int, ingredient: Ingredient)

            /**
             * Clears/Unassigns the ingredient at the specified index
             */
            fun clearIngredient(index: Int)

            var shape: ShapeStore

            interface ShapeStore {

                var symmetry: CraftingFormula.Shaped.ShapeSymmetry

                var trim: Boolean

            }

        }

    }

}