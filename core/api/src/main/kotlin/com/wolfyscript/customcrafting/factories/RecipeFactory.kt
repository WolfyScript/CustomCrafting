package com.wolfyscript.customcrafting.factories

import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.CustomRecipeMixing
import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing
import com.wolfyscript.customcrafting.recipes.CustomRecipeSmithing
import com.wolfyscript.customcrafting.recipes.CustomRecipeStonecutting
import com.wolfyscript.customcrafting.recipes.RecipeChoices
import com.wolfyscript.customcrafting.recipes.RecipeItemModifier
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.customcrafting.recipes.RecipeResult
import com.wolfyscript.customcrafting.recipes.RemainsIgnoreOptions
import com.wolfyscript.customcrafting.recipes.ResultAction
import com.wolfyscript.customcrafting.recipes.conditions.Condition
import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.recipes.process.ProcessRepairing
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
     * @see RecipeReference.of
     */
    fun <T : CustomRecipe<*, *>> createRecipeReference(key: Key, recipe: T): RecipeReference<T>

    /**
     * @see CraftingMatrixData.of
     */
    fun createMatrixData(ingredients: List<ScafallItemStack?>) : CraftingMatrixData

    /**
     * @see RecipeInput.CraftingRecipeInput.of
     */
    fun createCraftingRecipeInput(matrixData: CraftingMatrixData) : RecipeInput.CraftingRecipeInput

    /**
     * @see RecipeInput.GrindingRecipeInput.of
     */
    fun createGrindingRecipeInput(base: ScafallItemStack?, addition: ScafallItemStack?) : RecipeInput.GrindingRecipeInput

    /**
     * @see RecipeInput.MixingRecipeInput.of
     */
    fun createMixingRecipeInput(input: Collection<ScafallItemStack?>) : RecipeInput.MixingRecipeInput

    /**
     * @see RecipeInput.RepairingRecipeInput.of
     */
    fun createRepairingRecipeInput(base: ScafallItemStack, addition: ScafallItemStack?, itemName: String?) : RecipeInput.RepairingRecipeInput

    /**
     * @see RecipeInput.SmithingRecipeInput.of
     */
    fun createSmithingRecipeInput(template: ScafallItemStack?, base: ScafallItemStack?, addition: ScafallItemStack?) : RecipeInput.SmithingRecipeInput

    /**
     * @see RecipeInput.SingleSlotRecipeInput.of
     */
    fun createSingleSlotRecipeInput(source: ScafallItemStack) : RecipeInput.SingleSlotRecipeInput

    fun createRecipeConditions(
        conditions: List<Condition> = emptyList(),
    ) : RecipeConditions

    fun createRecipeCrafting(
        group: String = "",
        priority: Int = 0,
        conditions: RecipeConditions = createRecipeConditions(),
        formula: CraftingFormula,
        result: RecipeResult
    ) : CustomRecipeCrafting

    fun createRecipeCooking(
        group: String = "",
        priority: Int = 0,
        conditions: RecipeConditions = createRecipeConditions(),
        processing: CustomRecipeCooking.WorkstationProcessing,
        xp: Float,
        result: RecipeResult
    ) : CustomRecipeCooking

    fun createRecipeStonecutting(
        group: String = "",
        priority: Int = 0,
        conditions: RecipeConditions = createRecipeConditions(),
        source: Ingredient,
        result: RecipeResult,
        flattenResult: Boolean,
    ) : CustomRecipeStonecutting

    fun createRecipeMixing(
        group: String = "",
        priority: Int = 0,
        conditions: RecipeConditions = createRecipeConditions(),
        processingTime: Int,
        xp: Int,
        results: List<RecipeResult>,
        ingredients: List<Ingredient>,
        fluidRequirement: CustomRecipeMixing.FluidRequirement,
        campfireRequirement: CustomRecipeMixing.CampfireRequirement,
    ) : CustomRecipeMixing

    fun createRecipeRepairing(
        group: String = "",
        priority: Int = 0,
        conditions: RecipeConditions = createRecipeConditions(),
        process: ProcessRepairing,
        base: Ingredient,
        addition: Ingredient?,
    ) : CustomRecipeRepairing

    fun createRecipeSmithing(
        group: String = "",
        priority: Int = 0,
        conditions: RecipeConditions = createRecipeConditions(),
        template: Ingredient?,
        base: Ingredient,
        addition: Ingredient?,
        copyOptions: CustomRecipeSmithing.CopyOptions?,
        result: RecipeResult,
    ) : CustomRecipeSmithing

    interface CraftingFormulaFactory {

        fun createShapedFormula(
            mappedIngredients: Map<Char, Ingredient>,
            shape: CraftingFormula.Shaped.Shape,
        ) : CraftingFormula.Shaped

        fun createShape(
            rows: List<String>,
            trim: Boolean = true,
            symmetry: CraftingFormula.Shaped.ShapeSymmetry = createSymmetry()
        ) : CraftingFormula.Shaped.Shape

        fun createSymmetry(
            mirrorHorizontally: Boolean = false,
            mirrorVertically: Boolean = false,
            rotate: Boolean = false,
        ) : CraftingFormula.Shaped.ShapeSymmetry

        fun createShapelessFormula(ingredients: List<Ingredient>) : CraftingFormula.Shapeless

    }

    interface IngredientFactory {

        fun create(
            choices: RecipeChoices,
            matching: IngredientMatcher,
            consumption: IngredientConsumer,
        ) : Ingredient

        fun createConsumerKeep(modifier: RecipeItemModifier) : IngredientConsumer.Keep

        fun createConsumerReplace(replacement: ItemStackRef) : IngredientConsumer.Replace

        fun createConsumerConsume(remains: IngredientRemainder) : IngredientConsumer.Consume

        fun createMatcherExact() : IngredientMatcher.Exact

        fun createMatcherItem(
            mustContain: Set<Key> = emptySet(),
            mustNotContain: Set<Key> = emptySet()
        ) : IngredientMatcher.Item

        fun createRemainderDefault(
            ignore: RemainsIgnoreOptions
        ) : IngredientRemainder.Default

        fun createRemainderCustom(
            ignore: RemainsIgnoreOptions,
            remainder: ItemStackRef
        ) : IngredientRemainder.Custom

        fun createRemainsIgnoreOptions(
            vanilla: Boolean,
            others: Boolean
        ) : RemainsIgnoreOptions

    }

    interface ResultFactory {

        fun create(
            choices: RecipeChoices,
            modifier: RecipeItemModifier,
            actions: List<ResultAction> = listOf(),
            bulkActions: List<ResultAction> = listOf(),
            alwaysKeepPrevious: Boolean,
        ) : RecipeResult

    }

    interface RecipeItemFactory {

        fun createChoices(
            stacks: List<ItemStackRef> = emptyList(),
            tags: List<Key> = emptyList(),
        ) : RecipeChoices

        fun createModifier(transformations: List<RecipeItemModifier.Transformation>) : RecipeItemModifier

        fun createTransformation(
            ingredients: Array<Int>,
            transmuter: RecipeItemModifier.Transformation.Transmuter
        ) : RecipeItemModifier.Transformation

    }

}