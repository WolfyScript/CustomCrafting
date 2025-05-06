package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeData

/**
 * A custom recipe for the player inventory crafting grid, crafting table, and auto-crafter.
 *
 * The [formula] defines how the recipe is evaluated. (Shapeless or Shaped)
 *
 */
interface CustomRecipeCrafting : CustomRecipe<CustomRecipeCrafting> {

    /**
     * The formula used to evaluate the recipe.
     *
     * For example, a shapeless recipe would use [CraftingFormula.Shapeless]
     * and a shaped recipe would use [CraftingFormula.Shaped]
     */
    val formula: CraftingFormula

    /**
     * The result of the recipe.
     */
    val result: RecipeResult

    /**
     * Evaluates the recipe based on the given matrix.
     *
     * @return The resulting recipe data; or null if the recipe cannot be evaluated.
     */
    fun evaluate(matrix: CraftingMatrixData, context: EvaluationContext): RecipeData<CustomRecipeCrafting>?

}

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
// This could in theory be expanded to allow custom types from a type registry, but for now there is no need for that
@JsonSubTypes(
    JsonSubTypes.Type(value = CraftingFormula.Shapeless::class, name = "shapeless"),
    JsonSubTypes.Type(value = CraftingFormula.Shaped::class, name = "shaped")
)
@JsonPropertyOrder(value = ["type"])
interface CraftingFormula {

    val ingredients: List<Ingredient>

    fun evaluate(matrix: CraftingMatrixData): RecipeData<CustomRecipeCrafting>?

    /**
     * A crafting formula with a list of ingredients that can be arranged in any order
     */
    interface Shapeless : CraftingFormula

    /**
     * A crafting formula that requires ingredients to be arranged in a specified shape.
     *
     * The shape may allow ingredients to be arranged mirrored (see [ShapeSymmetry]).
     */
    interface Shaped : CraftingFormula {

        val shape: Shape

        val symmetry: ShapeSymmetry

        /**
         * Defines how the shape of the crafting grid may be mirrored.
         */
        interface ShapeSymmetry {
            /**
             * Whether the shape may be mirrored horizontally.
             */
            val horizontal: Boolean

            /**
             * Whether the shape may be mirrored vertically.
             */
            val vertical: Boolean

            /**
             * Whether the shape may be mirrored in both directions at once.
             */
            val rotate: Boolean
        }

        interface Shape {

            /**
             * A list of rows each with character keys that are associated with ingredients.
             * The length of the list is fixed at 3, and so is the length of each row.
             */
            val rows: List<String>

        }
        
    }
    
}
