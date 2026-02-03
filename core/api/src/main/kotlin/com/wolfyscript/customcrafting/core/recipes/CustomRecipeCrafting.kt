package com.wolfyscript.customcrafting.core.recipes

import com.fasterxml.jackson.annotation.*
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.factories.Factories
import com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

/**
 * A custom recipe for the player inventory crafting grid, crafting table, and auto-crafter.
 *
 * The [formula] defines how the recipe is evaluated. (Shapeless or Shaped)
 *
 */
interface CustomRecipeCrafting : CustomRecipe<com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.CraftingRecipeInput, com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult.Data> {

    companion object {

        fun of(
            group: String, priority: Int, conditions: com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions, formula: CraftingFormula, result: RecipeResult,
        ): CustomRecipeCrafting =
            _root_ide_package_.com.wolfyscript.customcrafting.core.factories.Factories.Companion.recipeFactory.createRecipeCrafting(group, priority, conditions, formula, result)

    }

    override val type: RecipeType<CustomRecipeCrafting>
        get() = RecipeTypes.crafting.resolveOrThrow()

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
    fun shrink(
        input: com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.CraftingRecipeInput,
        recipeEvaluationResult: com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult<com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult.Data, CustomRecipeCrafting>,
        context: EvaluationContext,
        count: Int,
        applyStacks: (index: Int, new: ScafallItemStack) -> Unit,
    )

}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSubTypes(
    JsonSubTypes.Type(value = CraftingFormula.Shapeless::class, name = "shapeless"),
    JsonSubTypes.Type(value = CraftingFormula.Shaped::class, name = "shaped")
)
@JsonPropertyOrder(value = ["type"])
interface CraftingFormula {

    fun evaluate(
        input: com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.CraftingRecipeInput,
        recipeCrafting: CustomRecipeCrafting,
    ): com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult.Data?

    /**
     * A crafting formula with a list of ingredients that can be arranged in any order
     */
    @JsonTypeName("shapeless")
    interface Shapeless : CraftingFormula {

        companion object {

            fun of(ingredients: List<com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient>): Shapeless =
                CustomCraftingProvider.get().factories.recipeFactory.craftingFormula.createShapelessFormula(ingredients)

        }

        val ingredients: List<com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient>

    }

    /**
     * A crafting formula that requires ingredients to be arranged in a specified shape.
     *
     * The shape may allow ingredients to be arranged mirrored (see [ShapeSymmetry]).
     */
    @JsonTypeName("shaped")
    interface Shaped : CraftingFormula {

        companion object {

            fun of(
                mappedIngredients: Map<Char, com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient>,
                shape: Shape,
            ): Shaped =
                CustomCraftingProvider.get().factories.recipeFactory.craftingFormula.createShapedFormula(
                    mappedIngredients,
                    shape
                )

        }

        val ingredients: List<com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient>

        val shape: Shape

        /**
         * Defines how the shape of the crafting grid may be mirrored.
         */
        interface ShapeSymmetry {

            companion object {

                fun of(
                    horizontal: Boolean,
                    vertical: Boolean,
                    rotate: Boolean,
                ) = CustomCraftingProvider.get().factories.recipeFactory.craftingFormula.createSymmetry(
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
        interface Shape {

            companion object {

                fun of(
                    rows: List<String>,
                    trim: Boolean = true,
                    symmetry: ShapeSymmetry,
                ): Shape = CustomCraftingProvider.get().factories.recipeFactory.craftingFormula.createShape(
                    rows, trim, symmetry
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
