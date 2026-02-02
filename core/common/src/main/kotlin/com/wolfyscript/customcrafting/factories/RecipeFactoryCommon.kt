package com.wolfyscript.customcrafting.factories

import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.recipes.conditions.Condition
import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.*
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.recipes.process.ProcessRepairing
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

class RecipeFactoryCommon : RecipeFactory {

    override val craftingFormula: RecipeFactory.CraftingFormulaFactory = CraftingFormulaFactoryImpl()
    override val recipeItem: RecipeFactory.RecipeItemFactory = RecipeItemFactoryImpl()
    override val ingredient: RecipeFactory.IngredientFactory = IngredientFactoryImpl()
    override val result: RecipeFactory.ResultFactory = ResultFactoryImpl()

    override fun <T : CustomRecipe<*, *>> createRecipeReference(
        key: Key,
        recipe: T,
    ): RecipeReference<T> {
        return RecipeReferenceImpl(key, recipe)
    }

    override fun createMatrixData(ingredients: List<ScafallItemStack?>): CraftingMatrixData {
        return ingredients.toCraftingMatrixData()
    }

    override fun createCraftingRecipeInput(matrixData: CraftingMatrixData): RecipeInput.CraftingRecipeInput {
        return CraftingRecipeInputImpl(matrixData)
    }

    override fun createGrindingRecipeInput(
        base: ScafallItemStack?,
        addition: ScafallItemStack?,
    ): RecipeInput.GrindingRecipeInput {
        return GrindingRecipeInputImpl(base, addition)
    }

    override fun createMixingRecipeInput(input: Collection<ScafallItemStack?>): RecipeInput.MixingRecipeInput {
        return MixingRecipeInputImpl(input)
    }

    override fun createRepairingRecipeInput(
        base: ScafallItemStack,
        addition: ScafallItemStack?,
        itemName: String?,
    ): RecipeInput.RepairingRecipeInput {
        return RepairingRecipeInputImpl(base, addition, itemName)
    }

    override fun createSmithingRecipeInput(
        template: ScafallItemStack?,
        base: ScafallItemStack?,
        addition: ScafallItemStack?,
    ): RecipeInput.SmithingRecipeInput {
        return SmithingRecipeInputImpl(template, base, addition)
    }

    override fun createSingleSlotRecipeInput(source: ScafallItemStack): RecipeInput.SingleSlotRecipeInput {
        return SingleSlotRecipeInputImpl(source)
    }

    override fun createRecipeConditions(conditions: List<Condition>): RecipeConditions {
        return RecipeConditionsImpl(conditions)
    }

    override fun createRecipeCrafting(
        group: String,
        priority: Int,
        conditions: RecipeConditions,
        formula: CraftingFormula,
        result: RecipeResult,
    ): CustomRecipeCrafting = CustomRecipeCraftingImpl(priority, conditions, formula, result, group)

    override fun createRecipeCooking(
        group: String,
        priority: Int,
        conditions: RecipeConditions,
        processing: CustomRecipeCooking.WorkstationProcessing,
        xp: Float,
        result: RecipeResult,
    ): CustomRecipeCooking = CustomRecipeCookingImpl(processing, result, priority, conditions, xp, group)

    override fun createRecipeStonecutting(
        group: String,
        priority: Int,
        conditions: RecipeConditions,
        source: Ingredient,
        result: RecipeResult,
        flattenResult: Boolean,
    ): CustomRecipeStonecutting =
        CustomRecipeStonecuttingImpl(priority, conditions, source, result, flattenResult, group)

    override fun createRecipeMixing(
        group: String,
        priority: Int,
        conditions: RecipeConditions,
        processingTime: Int,
        xp: Int,
        results: List<RecipeResult>,
        ingredients: List<Ingredient>,
        fluidRequirement: CustomRecipeMixing.FluidRequirement,
        campfireRequirement: CustomRecipeMixing.CampfireRequirement,
    ): CustomRecipeMixing = CustomRecipeMixingImpl(
        priority,
        conditions,
        processingTime,
        xp,
        results,
        ingredients,
        fluidRequirement,
        campfireRequirement,
        group
    )

    override fun createRecipeRepairing(
        group: String,
        priority: Int,
        conditions: RecipeConditions,
        process: ProcessRepairing,
        base: Ingredient,
        addition: Ingredient?,
    ): CustomRecipeRepairing = CustomRecipeRepairingImpl(priority, conditions, process, base, addition, group)

    override fun createRecipeSmithing(
        group: String,
        priority: Int,
        conditions: RecipeConditions,
        template: Ingredient?,
        base: Ingredient,
        addition: Ingredient?,
        copyOptions: CustomRecipeSmithing.CopyOptions?,
        result: RecipeResult,
    ): CustomRecipeSmithing =
        CustomRecipeSmithingImpl(priority, conditions, template, base, addition, copyOptions, result, group)

}

private class CraftingFormulaFactoryImpl() : RecipeFactory.CraftingFormulaFactory {

    override fun createShapedFormula(
        mappedIngredients: Map<Char, Ingredient>,
        shape: CraftingFormula.Shaped.Shape,
    ): CraftingFormula.Shaped = ShapedCraftingFormulaImpl(mappedIngredients, shape)

    override fun createShape(
        rows: List<String>,
        trim: Boolean,
        symmetry: CraftingFormula.Shaped.ShapeSymmetry,
    ): CraftingFormula.Shaped.Shape = ShapedCraftingFormulaImpl.ShapeImpl(rows, symmetry, trim)

    override fun createSymmetry(
        mirrorHorizontally: Boolean,
        mirrorVertically: Boolean,
        rotate: Boolean,
    ): CraftingFormula.Shaped.ShapeSymmetry =
        ShapedCraftingFormulaImpl.ShapeSymmetryImpl(mirrorHorizontally, mirrorVertically, rotate)

    override fun createShapelessFormula(ingredients: List<Ingredient>): CraftingFormula.Shapeless =
        ShapelessCraftingFormulaImpl(ingredients)

}

private class IngredientFactoryImpl : RecipeFactory.IngredientFactory {

    override fun create(
        choices: RecipeChoices,
        matching: IngredientMatcher,
        consumption: IngredientConsumer,
    ): Ingredient {
        return IngredientImpl(choices, matching, consumption)
    }

    override fun createConsumerKeep(modifier: RecipeItemModifier): IngredientConsumer.Keep {
        return IngredientConsumerKeepImpl(modifier)
    }

    override fun createConsumerReplace(replacement: ItemStackRef): IngredientConsumer.Replace {
        return IngredientConsumerReplaceImpl(replacement)
    }

    override fun createConsumerConsume(remains: IngredientRemainder): IngredientConsumer.Consume {
        return IngredientConsumerConsumeImpl(remains)
    }

    override fun createMatcherExact(): IngredientMatcher.Exact {
        return IngredientMatcherExactImpl()
    }

    override fun createMatcherItem(
        mustContain: Set<Key>,
        mustNotContain: Set<Key>,
    ): IngredientMatcher.Item {
        return IngredientMatcherItemImpl(mustContain, mustNotContain)
    }

    override fun createRemainderDefault(ignore: RemainsIgnoreOptions): IngredientRemainder.Default {
        return IngredientRemainderDefaultImpl(ignore)
    }

    override fun createRemainderCustom(
        ignore: RemainsIgnoreOptions,
        remainder: ItemStackRef,
    ): IngredientRemainder.Custom {
        return IngredientRemainderCustomImpl(ignore, remainder)
    }

    override fun createRemainsIgnoreOptions(
        vanilla: Boolean,
        others: Boolean,
    ): RemainsIgnoreOptions {
        return RemainsIgnoreOptionsImpl(vanilla, others)
    }

}

private class ResultFactoryImpl : RecipeFactory.ResultFactory {

    override fun create(
        choices: RecipeChoices,
        modifier: RecipeItemModifier,
        actions: List<ResultAction>,
        bulkActions: List<ResultAction>,
        alwaysKeepPrevious: Boolean,
    ): RecipeResult {
        return RecipeResultImpl(choices, modifier, actions, bulkActions, alwaysKeepPrevious)
    }

}

private class RecipeItemFactoryImpl() : RecipeFactory.RecipeItemFactory {

    override fun createChoices(
        stacks: List<ItemStackRef>,
        tags: List<Key>,
    ): RecipeChoices {
        return RecipeChoicesImpl(stacks, tags)
    }

    override fun createModifier(transformations: List<RecipeItemModifier.Transformation>): RecipeItemModifier {
        return RecipeItemModifierImpl(transformations)
    }

    override fun createTransformation(
        ingredients: Array<Int>,
        transmuter: RecipeItemModifier.Transformation.Transmuter
    ): RecipeItemModifier.Transformation {
        return RecipeItemModifierImpl.ResultModifierTransformationImpl(ingredients, transmuter)
    }

}