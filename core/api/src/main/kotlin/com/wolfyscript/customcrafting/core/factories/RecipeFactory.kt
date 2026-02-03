package com.wolfyscript.customcrafting.core.factories

import com.wolfyscript.customcrafting.core.recipes.CraftingFormula
import com.wolfyscript.customcrafting.core.recipes.CustomRecipe
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeCooking
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeMixing
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeRepairing
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeSmithing
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeStonecutting
import com.wolfyscript.customcrafting.core.recipes.RecipeChoices
import com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier
import com.wolfyscript.customcrafting.core.recipes.RecipeReference
import com.wolfyscript.customcrafting.core.recipes.RecipeResult
import com.wolfyscript.customcrafting.core.recipes.RemainsIgnoreOptions
import com.wolfyscript.customcrafting.core.recipes.ResultAction
import com.wolfyscript.customcrafting.core.recipes.conditions.Condition
import com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.core.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.core.recipes.process.ProcessRepairing
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

/**
 * Factory functions related to custom recipes.
 *
 * When using Kotlin these functions are usually available via convenient extension or top-level functions.
 */
interface RecipeFactory {

    val craftingFormula: CraftingFormulaFactory

    val recipeItem: RecipeItemFactory

    val ingredient: IngredientFactory

    val result: ResultFactory

    /**
     * Creates a RecipeReference from the specified [key] and [recipe]
     *
     * @see com.wolfyscript.customcrafting.core.recipes.RecipeReference.Companion.of
     */
    fun <T : com.wolfyscript.customcrafting.core.recipes.CustomRecipe<*, *>> createRecipeReference(key: Key, recipe: T): com.wolfyscript.customcrafting.core.recipes.RecipeReference<T>

    /**
     * @see com.wolfyscript.customcrafting.core.recipes.data.CraftingMatrixData.Companion.of
     */
    fun createMatrixData(ingredients: List<ScafallItemStack?>) : com.wolfyscript.customcrafting.core.recipes.data.CraftingMatrixData

    /**
     * @see com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.CraftingRecipeInput.Companion.of
     */
    fun createCraftingRecipeInput(matrixData: com.wolfyscript.customcrafting.core.recipes.data.CraftingMatrixData) : com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.CraftingRecipeInput

    /**
     * @see com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.GrindingRecipeInput.Companion.of
     */
    fun createGrindingRecipeInput(base: ScafallItemStack?, addition: ScafallItemStack?) : com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.GrindingRecipeInput

    /**
     * @see com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.MixingRecipeInput.Companion.of
     */
    fun createMixingRecipeInput(input: Collection<ScafallItemStack?>) : com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.MixingRecipeInput

    /**
     * @see com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.RepairingRecipeInput.Companion.of
     */
    fun createRepairingRecipeInput(base: ScafallItemStack, addition: ScafallItemStack?, itemName: String?) : com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.RepairingRecipeInput

    /**
     * @see com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.SmithingRecipeInput.Companion.of
     */
    fun createSmithingRecipeInput(template: ScafallItemStack?, base: ScafallItemStack?, addition: ScafallItemStack?) : com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.SmithingRecipeInput

    /**
     * @see com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.SingleSlotRecipeInput.Companion.of
     */
    fun createSingleSlotRecipeInput(source: ScafallItemStack) : com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.SingleSlotRecipeInput

    fun createRecipeConditions(
        conditions: List<com.wolfyscript.customcrafting.core.recipes.conditions.Condition> = emptyList(),
    ) : com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions

    fun createRecipeCrafting(
        group: String = "",
        priority: Int = 0,
        conditions: com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions = createRecipeConditions(),
        formula: com.wolfyscript.customcrafting.core.recipes.CraftingFormula,
        result: com.wolfyscript.customcrafting.core.recipes.RecipeResult
    ) : com.wolfyscript.customcrafting.core.recipes.CustomRecipeCrafting

    fun createRecipeCooking(
        group: String = "",
        priority: Int = 0,
        conditions: com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions = createRecipeConditions(),
        processing: com.wolfyscript.customcrafting.core.recipes.CustomRecipeCooking.WorkstationProcessing,
        xp: Float,
        result: com.wolfyscript.customcrafting.core.recipes.RecipeResult
    ) : com.wolfyscript.customcrafting.core.recipes.CustomRecipeCooking

    fun createRecipeStonecutting(
        group: String = "",
        priority: Int = 0,
        conditions: com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions = createRecipeConditions(),
        source: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient,
        result: com.wolfyscript.customcrafting.core.recipes.RecipeResult,
        flattenResult: Boolean,
    ) : com.wolfyscript.customcrafting.core.recipes.CustomRecipeStonecutting

    fun createRecipeMixing(
        group: String = "",
        priority: Int = 0,
        conditions: com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions = createRecipeConditions(),
        processingTime: Int,
        xp: Int,
        results: List<com.wolfyscript.customcrafting.core.recipes.RecipeResult>,
        ingredients: List<com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient>,
        fluidRequirement: com.wolfyscript.customcrafting.core.recipes.CustomRecipeMixing.FluidRequirement,
        campfireRequirement: com.wolfyscript.customcrafting.core.recipes.CustomRecipeMixing.CampfireRequirement,
    ) : com.wolfyscript.customcrafting.core.recipes.CustomRecipeMixing

    fun createRecipeRepairing(
        group: String = "",
        priority: Int = 0,
        conditions: com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions = createRecipeConditions(),
        process: com.wolfyscript.customcrafting.core.recipes.process.ProcessRepairing,
        base: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient,
        addition: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient?,
    ) : com.wolfyscript.customcrafting.core.recipes.CustomRecipeRepairing

    fun createRecipeSmithing(
        group: String = "",
        priority: Int = 0,
        conditions: com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions = createRecipeConditions(),
        template: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient?,
        base: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient,
        addition: com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient?,
        copyOptions: com.wolfyscript.customcrafting.core.recipes.CustomRecipeSmithing.CopyOptions?,
        result: com.wolfyscript.customcrafting.core.recipes.RecipeResult,
    ) : com.wolfyscript.customcrafting.core.recipes.CustomRecipeSmithing

    interface CraftingFormulaFactory {

        fun createShapedFormula(
            mappedIngredients: Map<Char, com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient>,
            shape: com.wolfyscript.customcrafting.core.recipes.CraftingFormula.Shaped.Shape,
        ) : com.wolfyscript.customcrafting.core.recipes.CraftingFormula.Shaped

        fun createShape(
            rows: List<String>,
            trim: Boolean = true,
            symmetry: com.wolfyscript.customcrafting.core.recipes.CraftingFormula.Shaped.ShapeSymmetry = createSymmetry()
        ) : com.wolfyscript.customcrafting.core.recipes.CraftingFormula.Shaped.Shape

        fun createSymmetry(
            mirrorHorizontally: Boolean = false,
            mirrorVertically: Boolean = false,
            rotate: Boolean = false,
        ) : com.wolfyscript.customcrafting.core.recipes.CraftingFormula.Shaped.ShapeSymmetry

        fun createShapelessFormula(ingredients: List<com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient>) : com.wolfyscript.customcrafting.core.recipes.CraftingFormula.Shapeless

    }

    interface IngredientFactory {

        fun create(
            choices: com.wolfyscript.customcrafting.core.recipes.RecipeChoices,
            matching: com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatcher,
            consumption: com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientConsumer,
        ) : com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient

        fun createConsumerKeep(modifier: com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier) : com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientConsumer.Keep

        fun createConsumerReplace(replacement: ItemStackRef) : com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientConsumer.Replace

        fun createConsumerConsume(remains: com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientRemainder) : com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientConsumer.Consume

        fun createMatcherExact() : com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatcher.Exact

        fun createMatcherItem(
            mustContain: Set<Key> = emptySet(),
            mustNotContain: Set<Key> = emptySet()
        ) : com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatcher.Item

        fun createRemainderDefault(
            ignore: com.wolfyscript.customcrafting.core.recipes.RemainsIgnoreOptions
        ) : com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientRemainder.Default

        fun createRemainderCustom(
            ignore: com.wolfyscript.customcrafting.core.recipes.RemainsIgnoreOptions,
            remainder: ItemStackRef
        ) : com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientRemainder.Custom

        fun createRemainsIgnoreOptions(
            vanilla: Boolean,
            others: Boolean
        ) : com.wolfyscript.customcrafting.core.recipes.RemainsIgnoreOptions

    }

    interface ResultFactory {

        fun create(
            choices: com.wolfyscript.customcrafting.core.recipes.RecipeChoices,
            modifier: com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier,
            actions: List<com.wolfyscript.customcrafting.core.recipes.ResultAction> = listOf(),
            bulkActions: List<com.wolfyscript.customcrafting.core.recipes.ResultAction> = listOf(),
            alwaysKeepPrevious: Boolean,
        ) : com.wolfyscript.customcrafting.core.recipes.RecipeResult

    }

    interface RecipeItemFactory {

        fun createChoices(
            stacks: List<ItemStackRef> = emptyList(),
            tags: List<Key> = emptyList(),
        ) : com.wolfyscript.customcrafting.core.recipes.RecipeChoices

        fun createModifier(transformations: List<com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier.Transformation>) : com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier

        fun createTransformation(
            ingredients: Array<Int>,
            transmuter: com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier.Transformation.Transmuter
        ) : com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier.Transformation

    }

}