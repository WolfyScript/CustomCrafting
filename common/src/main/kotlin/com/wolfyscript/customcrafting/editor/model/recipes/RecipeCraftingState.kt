package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.editor.model.recipes.result.ResultModel
import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient

class RecipeCraftingStateFactory() : RecipeModel.RecipeTypeSpecificModel.Factory<CustomRecipeCrafting> {

    override val recipeType: RecipeType<CustomRecipeCrafting> by lazy { RecipeTypes.crafting.resolveOrThrow() }

    override fun edit(recipe: CustomRecipeCrafting): RecipeModel.RecipeTypeSpecificModel<CustomRecipeCrafting> {
        return RecipeCraftingModelImpl() // TODO: load recipe into state
    }

    override fun create(): RecipeModel.RecipeTypeSpecificModel<CustomRecipeCrafting> {
        return RecipeCraftingModelImpl()
    }

}

class IngredientCollectionModelStateImpl(
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

    override fun remove(index: Int) {
        ingredients.removeAt(index)
    }

}

class RecipeCraftingModelImpl(
    override val result: ResultModel = ResultModelImpl(),
    override var formula: RecipeCraftingModel.CraftingFormulaModel<*> = ShapedCraftingFormulaModel(),
    override val ingredientCollection: RecipeCraftingModel.IngredientCollectionModel = IngredientCollectionModelStateImpl(),
) : RecipeCraftingModel {

    override fun setFormulaType(type: Class<out CraftingFormula>) {
        val previousFormula = formula
        formula = when (type) {
            CraftingFormula.Shaped::class.java -> ShapedCraftingFormulaModel()
            CraftingFormula.Shapeless::class.java -> ShapelessCraftingFormulaModel()
            else -> ShapedCraftingFormulaModel()
        }
        // TODO: copy properties like ingredients to not reset them
    }

    override fun complete(common: RecipeModel<CustomRecipeCrafting>): Result<CustomRecipeCrafting> {
        val completedFormula = formula.complete().getOrElse {
            return Result.failure(IllegalStateException("Failed to create crafting recipe: Invalid formula", it))
        }
        val completedResult = result.complete().getOrElse {
            return Result.failure(IllegalStateException("Failed to create crafting recipe: Invalid result", it))
        }

        val recipe = CustomRecipeCraftingImpl(
            priority = common.priority,
            conditions = common.condition?.complete()?.getOrNull() ?: RecipeConditionsImpl(),
            formula = completedFormula,
            result = completedResult
        )
        return Result.success(recipe)
    }

}

class ShapelessCraftingFormulaModel : RecipeCraftingModel.CraftingFormulaModel.Shapeless {

    override val ingredients: MutableList<IngredientModel> = mutableListOf()

    override fun addIngredient(ingredient: Ingredient) {
        if (ingredients.size < 9) {
            ingredients.add(CustomIngredientModelImpl.loadFrom(ingredient))
        }
    }

    override fun addIngredient(ingredient: IngredientModel) {
        if (ingredients.size < 9) {
            ingredients.add(ingredient)
        }
    }

    override fun removeIngredient(index: Int) {
        ingredients.removeAt(index)
    }

    override fun complete(): Result<CraftingFormula.Shapeless> {
        if (ingredients.isEmpty()) {
            return Result.failure(IllegalStateException("Failed to create shapeless formula: Must have at least 1 ingredient"))
        }

        val completedIngredients = mutableListOf<Ingredient>()
        for ((index, ingredientState) in ingredients.withIndex()) {
            val ingredient = ingredientState.complete().getOrElse {
                return Result.failure(
                    IllegalStateException(
                        "Failed to create shapeless formula: invalid ingredient at index $index",
                        it
                    )
                )
            }
            completedIngredients.add(ingredient)
        }

        return Result.success(ShapelessCraftingFormulaImpl(completedIngredients))
    }

}

class ShapedCraftingFormulaModel(
    // TODO: Make immutable
    override val ingredients: MutableList<IngredientModel?> = arrayOfNulls<IngredientModel?>(9).toMutableList(),
    private val ingredientToId: MutableMap<IngredientModel, Char> = mutableMapOf(),
    override var shape: RecipeCraftingModel.CraftingFormulaModel.Shaped.ShapeState = ShapeState(ingredients, ingredientToId),
) : RecipeCraftingModel.CraftingFormulaModel.Shaped {

    override fun assignIngredient(
        index: Int,
        ingredient: Ingredient,
    ) {
        if (index > 0 && index < ingredients.size) {
            val state = CustomIngredientModelImpl.loadFrom(ingredient)
            ingredients[index] = state

            ingredientToId.clear()
            for ((index, ingredientState) in ingredients.distinct().withIndex()) {
                if (ingredientState != null) {
                    ingredientToId[ingredientState] = index.digitToChar()
                }
            }
        }
    }

    override fun clearIngredient(index: Int) {
        if (index > 0 && index < ingredients.size) {
            ingredients.removeAt(index)
        }
    }

    override fun complete(): Result<CraftingFormula.Shaped> {
        val mappedIngredients = buildMap {
            for ((ingredient, key) in ingredientToId.entries) {
                this[key] = ingredient.complete().getOrElse {
                    return Result.failure(
                        IllegalStateException(
                            "Failed to create shaped formula: invalid ingredient $key",
                            it
                        )
                    )
                }
            }
        }
        val completedShape = shape.complete().getOrElse {
            return Result.failure(
                IllegalStateException(
                    "Failed to create shaped formula: failed to complete shape",
                    it
                )
            )
        }

        val shaped = ShapedCraftingFormulaImpl(mappedIngredients, completedShape)
        return Result.success(shaped)
    }

    class ShapeState(
        val ingredients: List<IngredientModel?>,
        private val ingredientToId: Map<IngredientModel, Char>,
        override var symmetry: CraftingFormula.Shaped.ShapeSymmetry = ShapedCraftingFormulaImpl.ShapeSymmetryImpl(
            horizontal = false,
            vertical = false,
            rotate = false
        ),
        override var trim: Boolean = true,
    ) : RecipeCraftingModel.CraftingFormulaModel.Shaped.ShapeState {

        override fun complete(): Result<CraftingFormula.Shaped.Shape> {
            val rows: MutableList<String> = mutableListOf("", "", "")
            for ((index, ingredientState) in ingredients.withIndex()) {
                val char: Char = ingredientToId[ingredientState] ?: ' '
                rows[index / 3] += char
            }
            return Result.success(ShapedCraftingFormulaImpl.ShapeImpl(rows, symmetry, trim))
        }
    }

}

