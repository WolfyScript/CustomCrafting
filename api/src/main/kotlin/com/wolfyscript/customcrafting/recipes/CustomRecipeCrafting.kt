package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

/**
 * A custom recipe for the player inventory crafting grid, crafting table, and auto-crafter.
 *
 * The [formula] defines how the recipe is evaluated. (Shapeless or Shaped)
 *
 */
interface CustomRecipeCrafting : CustomRecipe<RecipeInput.CraftingRecipeInput, CustomRecipeCrafting> {

    override val type: RecipeType<CustomRecipeCrafting>
        get() = RecipeTypes.crafting

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
     * Shrinks the given matrix by the given count (if possible).
     *
     * @param applyStacks A function that is called for each stack in the matrix that is shrunk.
     */
    fun shrink(input: RecipeInput.CraftingRecipeInput, recipeData: RecipeData<CustomRecipeCrafting>, context: EvaluationContext, count: Int, applyStacks: (index: Int, new: ItemStack) -> Unit)

}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
// This could in theory be expanded to allow custom types from a type registry, but for now there is no need for that
@JsonSubTypes(
    JsonSubTypes.Type(value = CraftingFormula.Shapeless::class, name = "shapeless"),
    JsonSubTypes.Type(value = CraftingFormula.Shaped::class, name = "shaped")
)
@JsonPropertyOrder(value = ["type"])
interface CraftingFormula {

    fun evaluate(input: RecipeInput.CraftingRecipeInput, recipeCrafting: CustomRecipeCrafting): RecipeData<CustomRecipeCrafting>?

    /**
     * A crafting formula with a list of ingredients that can be arranged in any order
     */
    interface Shapeless : CraftingFormula {

        val ingredients: List<Ingredient>

    }

    /**
     * A crafting formula that requires ingredients to be arranged in a specified shape.
     *
     * The shape may allow ingredients to be arranged mirrored (see [ShapeSymmetry]).
     */
    interface Shaped : CraftingFormula {

        val ingredients: List<Ingredient>

        val shape: Shape

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

            val symmetry: ShapeSymmetry

            val width: Int

            val height: Int

            /**
             * Whether the shape should be trimmed to remove leading and trailing empty rows and columns.
             * When disabled even a shape only occupying a 2x2 area will only work if placed exactly how defined by shape [rows].
             */
            val trim: Boolean

            /**
             * A list of rows each with character keys that are associated with ingredients.
             * The length of the list is fixed at 3, and so is the length of each row.
             */
            val rows: List<String>

            /**
             * A list of possible arrangements the shape can be in.
             * The 2d shapes are represented as 1d arrays of the ingredient indices based on the [ingredientIndices].
             */
            val variations: List<Array<Int>>

            /**
             * List of the ingredient keys in the shape in order of appearance.
             */
            val ingredientIndices: List<Char>

        }
        
    }
    
}
