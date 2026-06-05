package com.wolfyscript.customcrafting.core.recipe

import com.fasterxml.jackson.annotation.*
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSubTypes(
    JsonSubTypes.Type(value = CraftingFormula.Shapeless::class, name = "shapeless"),
    JsonSubTypes.Type(value = CraftingFormula.Shaped::class, name = "shaped")
)
@JsonPropertyOrder(value = ["type"])
interface CraftingFormula {

    /**
     * Evaluates the crafting formula based on the given input and recipe crafting.
     *
     * @param input The input for the recipe evaluation.
     * @param recipeCrafting The custom recipe to be used for evaluation.
     * @return The data containing the result of the recipe evaluation, or null if the evaluation fails.
     */
    fun evaluate(
        input: RecipeInput.CraftingRecipeInput,
        recipeCrafting: CustomRecipeCrafting,
    ): RecipeEvaluationResult.Data?

    /**
     * A crafting formula with a list of ingredients that can be arranged in any order
     */
    @JsonTypeName("shapeless")
    @JsonDeserialize(`as` = CraftingFormulaShapelessImpl::class)
    interface Shapeless : CraftingFormula {

        companion object {

            fun of(ingredients: List<Ingredient>): Shapeless =
                CraftingFormulaShapelessImpl(ingredients)

        }

        /**
         * A list of ingredients required for the crafting formula.
         * The order of ingredients does not matter.
         */
        val ingredients: List<Ingredient>

    }

    /**
     * A crafting formula that requires ingredients to be arranged in a specified shape.
     *
     * The shape may allow ingredients to be arranged mirrored (see [ShapeSymmetry]).
     */
    @JsonTypeName("shaped")
    @JsonDeserialize(`as` = CraftingFormulaShapedImpl::class)
    interface Shaped : CraftingFormula {

        companion object {

            fun of(
                mappedIngredients: Map<Char, Ingredient>,
                shape: Shape,
            ): Shaped =
                CraftingFormulaShapedImpl(
                    mappedIngredients,
                    shape
                )

        }

        /**
         * A list of ingredients in the crafting formula in order of their appearance in the shape.
         */
        val ingredients: List<Ingredient>

        /**
         * The shape of this crafting formula.
         */
        val shape: Shape

        /**
         * Defines how the shape of the crafting grid may be mirrored.
         */
        @JsonDeserialize(`as` = CraftingFormulaShapedImpl.ShapeSymmetryImpl::class)
        interface ShapeSymmetry {

            companion object {

                fun of(
                    horizontal: Boolean = false,
                    vertical: Boolean = false,
                    rotate: Boolean = false,
                ): ShapeSymmetry = CraftingFormulaShapedImpl.ShapeSymmetryImpl(
                    horizontal,
                    vertical,
                    rotate
                )

            }

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

        /**
         * The shape of the [Shaped] formula.
         *
         * Pre-calculates the [variations] of the shape based on the [symmetry] upon initialization.
         */
        @JsonDeserialize(`as` = CraftingFormulaShapedImpl.ShapeImpl::class)
        interface Shape {

            companion object {

                fun of(
                    rows: List<String>,
                    trim: Boolean = true,
                    symmetry: ShapeSymmetry = ShapeSymmetry.of(),
                ): Shape = CraftingFormulaShapedImpl.ShapeImpl(
                    rows, symmetry, trim
                )

            }

            val symmetry: ShapeSymmetry

            /**
             * The width of the final shape (i.e. trimmed shape if [trim] is true)
             */
            @get:JsonIgnore
            val width: Int

            /**
             * The height of the final shape (i.e. trimmed shape if [trim] is true)
             */
            @get:JsonIgnore
            val height: Int

            /**
             * Whether the shape should be trimmed to remove leading and trailing empty rows and columns.
             * When disabled, even a shape only occupying a 2x2 area will only work if placed exactly how defined by shape [rows].
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
            @get:JsonIgnore
            val variations: List<Array<Int>>

            /**
             * List of the ingredient keys in the shape in order of appearance.
             */
            @get:JsonIgnore
            val ingredientIndices: List<Char>

        }

    }

}