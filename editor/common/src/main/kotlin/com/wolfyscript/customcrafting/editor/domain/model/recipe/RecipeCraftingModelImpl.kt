package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.CustomIngredientModelImpl
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientModelRefImpl
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ResultModelImpl
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientModelRef
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ResultModel
import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import kotlin.text.isBlank

internal class RecipeCraftingModelFactory() : RecipeModel.RecipeTypeSpecificModel.Factory<CustomRecipeCrafting> {

    override val recipeType: RecipeType<CustomRecipeCrafting> by lazy { RecipeTypes.crafting.resolveOrThrow() }

    override fun edit(recipe: CustomRecipeCrafting): RecipeModel.RecipeTypeSpecificModel<CustomRecipeCrafting> {
        return RecipeCraftingModelImpl() // TODO: load recipe into state
    }

    override fun create(): RecipeModel.RecipeTypeSpecificModel<CustomRecipeCrafting> {
        return RecipeCraftingModelImpl()
    }

}

internal data class IngredientCollectionModelImpl(
    override val ingredients: MutableList<IngredientModel> = mutableListOf(),
) : RecipeCraftingModel.IngredientCollectionModel {

    override fun addNew() {
        add(CustomIngredientModelImpl())
    }

    override fun add(ingredient: IngredientModel) {
        if (ingredients.size < 9) {
            ingredients.add(ingredient)
        }
    }

    override fun set(
        index: Int,
        ingredient: IngredientModel,
    ) {
        ingredients[index] = ingredient
    }

    override fun remove(index: Int) {
        ingredients.removeAt(index)
    }

}

internal data class RecipeCraftingModelImpl(
    override var result: ResultModel = ResultModelImpl(),
    override var formula: RecipeCraftingModel.CraftingFormulaModel<*> = ShapedCraftingFormulaModel(),
    override val ingredientCollection: RecipeCraftingModel.IngredientCollectionModel = IngredientCollectionModelImpl(),
) : RecipeCraftingModel {

    override fun setFormulaType(type: Class<out CraftingFormula>) {
        val ingredients = when (val previousFormula = formula) {
            is RecipeCraftingModel.CraftingFormulaModel.Shaped -> previousFormula.ingredientRefs
            is RecipeCraftingModel.CraftingFormulaModel.Shapeless -> previousFormula.ingredientRefs
            else -> emptyList()
        }

        formula = when (type) {
            CraftingFormula.Shaped::class.java -> {
                val newList = ArrayList<IngredientModelRef?>(9)
                for (i in 0 until 9) {
                    newList.add(ingredients.getOrElse(i) { null })
                }
                ShapedCraftingFormulaModel(ingredientRefs = newList)
            }

            CraftingFormula.Shapeless::class.java -> ShapelessCraftingFormulaModel(
                ingredientRefs = ingredients.filterNotNull().toMutableList()
            )

            else -> ShapedCraftingFormulaModel()
        }
    }

    override fun complete(common: RecipeModel<CustomRecipeCrafting>): Result<CustomRecipeCrafting> {
        val completedFormula = formula.complete(ingredientCollection).getOrElse {
            return Result.failure(IllegalStateException("Failed to create crafting recipe: Invalid formula", it))
        }
        val completedResult = result.complete().getOrElse {
            return Result.failure(IllegalStateException("Failed to create crafting recipe: Invalid result", it))
        }

        val recipe = CustomCraftingProvider.get().factories.recipeFactory.createRecipeCrafting(
            group = "", // TODO
            priority = common.priority,
            conditions = common.condition?.complete()?.getOrNull() ?: RecipeConditions.of(),
            formula = completedFormula,
            result = completedResult
        )
        return Result.success(recipe)
    }

}

internal data class ShapelessCraftingFormulaModel(
    override val ingredientRefs: MutableList<IngredientModelRef> = mutableListOf(),
) : RecipeCraftingModel.CraftingFormulaModel.Shapeless {

    override fun assignIngredient(
        index: Int,
        collectionIndex: Int,
    ) {
        if (index >= 0 && index < ingredientRefs.size) {
            ingredientRefs[index] = IngredientModelRefImpl(collectionIndex)
        } else if (index > ingredientRefs.size && ingredientRefs.size < 9) {
            ingredientRefs.add(IngredientModelRefImpl(collectionIndex))
        }
    }

    override fun unassignIngredient(index: Int) {
        if (index >= 0 && ingredientRefs.size < 9) {
            ingredientRefs.removeAt(index)
        }
    }

    override fun complete(collection: RecipeCraftingModel.IngredientCollectionModel): Result<CraftingFormula.Shapeless> {
        val completedIngredients = mutableListOf<Ingredient>()
        for ((index, ref) in ingredientRefs.withIndex()) {
            val resolved = ref.resolveFor(collection)
                ?: return Result.failure(IllegalStateException("Failed to resolve ingredient $ref: Missing ingredient in collection"))
            val ingredient = resolved.complete().getOrElse {
                return Result.failure(
                    IllegalStateException(
                        "Failed to create shapeless formula: invalid ingredient at index $index",
                        it
                    )
                )
            }
            completedIngredients.add(ingredient)
        }

        if (completedIngredients.isEmpty()) {
            return Result.failure(IllegalStateException("Failed to create shapeless formula: Must have at least 1 ingredient"))
        }

        return Result.success(CraftingFormula.Shapeless.of(completedIngredients))
    }

}

internal data class ShapedCraftingFormulaModel(
    // TODO: Make immutable
    override val ingredientRefs: MutableList<IngredientModelRef?> = arrayOfNulls<IngredientModelRef?>(9).toMutableList(),
    override var shape: RecipeCraftingModel.CraftingFormulaModel.Shaped.ShapeModel = ShapeModel(
        ingredientRefs
    ),
) : RecipeCraftingModel.CraftingFormulaModel.Shaped {

    override fun assignIngredient(
        index: Int,
        collectionIndex: Int,
    ) {
        if (index >= 0 && index < ingredientRefs.size) {
            ingredientRefs[index] = IngredientModelRefImpl(collectionIndex)
        }
    }

    override fun unassignIngredient(index: Int) {
        if (index >= 0 && index < ingredientRefs.size) {
            ingredientRefs[index] = null
        }
    }

    override fun complete(collection: RecipeCraftingModel.IngredientCollectionModel): Result<CraftingFormula.Shaped> {
        val mappedIngredients = buildMap {
            for (ref in ingredientRefs) {
                val resolvedIngredient = ref?.resolveFor(collection)
                    ?: return Result.failure(IllegalStateException("Failed to resolve ingredient for $ref: Missing ingredient in collection"))
                val shapeId = ref.toShapeId()
                this[shapeId] = resolvedIngredient.complete().getOrElse {
                    return Result.failure(
                        IllegalStateException(
                            "Failed to create shaped formula: invalid ingredient at index ${ref.indexInCollection}",
                            it
                        )
                    )
                }
            }
        }
        if (mappedIngredients.isEmpty()) {
            return Result.failure(IllegalStateException("Failed to create shaped formula: Must have at least 1 ingredient"))
        }
        val completedShape = shape.complete().getOrElse {
            return Result.failure(
                IllegalStateException(
                    "Failed to create shaped formula: failed to complete shape",
                    it
                )
            )
        }
        if (completedShape.rows.all { it.isBlank() }) {
            return Result.failure(IllegalStateException("Failed to create shaped formula: Must have a defined shape"))
        }

        val shaped = CraftingFormula.Shaped.of(mappedIngredients, completedShape)
        return Result.success(shaped)
    }

    class ShapeModel(
        val ingredientRefs: List<IngredientModelRef?>,
        override var symmetry: CraftingFormula.Shaped.ShapeSymmetry = CraftingFormula.Shaped.ShapeSymmetry.of(
            horizontal = false,
            vertical = false,
            rotate = false
        ),
        override var trim: Boolean = true,
    ) : RecipeCraftingModel.CraftingFormulaModel.Shaped.ShapeModel {

        override fun complete(): Result<CraftingFormula.Shaped.Shape> {
            val rows: MutableList<String> = mutableListOf("", "", "")
            for ((index, ref) in ingredientRefs.withIndex()) {
                rows[index / 3] += ref?.toShapeId() ?: ' '
            }
            return Result.success(CraftingFormula.Shaped.Shape.of(rows, trim, symmetry))
        }
    }

}

