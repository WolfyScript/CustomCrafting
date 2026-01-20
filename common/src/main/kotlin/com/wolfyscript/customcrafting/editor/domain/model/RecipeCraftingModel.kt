package com.wolfyscript.customcrafting.editor.domain.model

import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeCraftingModel
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeModel
import com.wolfyscript.customcrafting.editor.domain.recipes.result.ResultModel
import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import kotlin.collections.get

class RecipeCraftingModelFactory() : RecipeModel.RecipeTypeSpecificModel.Factory<CustomRecipeCrafting> {

    override val recipeType: RecipeType<CustomRecipeCrafting> by lazy { RecipeTypes.crafting.resolveOrThrow() }

    override fun edit(recipe: CustomRecipeCrafting): RecipeModel.RecipeTypeSpecificModel<CustomRecipeCrafting> {
        return RecipeCraftingModelImpl() // TODO: load recipe into state
    }

    override fun create(): RecipeModel.RecipeTypeSpecificModel<CustomRecipeCrafting> {
        return RecipeCraftingModelImpl()
    }

}

data class IngredientCollectionModelImpl(
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

data class RecipeCraftingModelImpl(
    override var result: ResultModel = ResultModelImpl(),
    override var formula: RecipeCraftingModel.CraftingFormulaModel<*> = ShapedCraftingFormulaModel(),
    override val ingredientCollection: RecipeCraftingModel.IngredientCollectionModel = IngredientCollectionModelImpl(),
) : RecipeCraftingModel {

    override fun setFormulaType(type: Class<out CraftingFormula>) {
        val ingredients = when(val previousFormula = formula) {
            is RecipeCraftingModel.CraftingFormulaModel.Shaped -> previousFormula.ingredients
            is RecipeCraftingModel.CraftingFormulaModel.Shapeless -> previousFormula.ingredients
            else -> emptyList()
        }

        formula = when (type) {
            CraftingFormula.Shaped::class.java -> {
                val newList = ArrayList<IngredientModel?>(9)
                for (i in 0 until 9) {
                    newList.add(ingredients.getOrElse(i) { null })
                }
                ShapedCraftingFormulaModel(ingredients = newList,)
            }
            CraftingFormula.Shapeless::class.java -> ShapelessCraftingFormulaModel(
                ingredients = ingredients.filterNotNull().toMutableList()
            )
            else -> ShapedCraftingFormulaModel()
        }
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

data class ShapelessCraftingFormulaModel(
    override val ingredients: MutableList<IngredientModel> = mutableListOf()
) : RecipeCraftingModel.CraftingFormulaModel.Shapeless {

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

    override fun assignIngredient(
        index: Int,
        ingredient: IngredientModel,
    ) {
        if (index >= 0 && index < ingredients.size) {
            ingredients[index] = ingredient
        } else if (index > ingredients.size){
            addIngredient(ingredient)
        }
    }

    override fun unassignIngredient(index: Int) {
        if (index >= 0 && ingredients.size < 9) {
            ingredients.removeAt(index)
        }
    }

    override fun getIngredient(index: Int): IngredientModel? {
        if (index > 0 && index < ingredients.size) {
            return ingredients[index]
        }
        return null
    }

    override fun complete(): Result<CraftingFormula.Shapeless> {
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

        if (completedIngredients.isEmpty()) {
            return Result.failure(IllegalStateException("Failed to create shapeless formula: Must have at least 1 ingredient"))
        }

        return Result.success(ShapelessCraftingFormulaImpl(completedIngredients))
    }

}

data class ShapedCraftingFormulaModel(
    // TODO: Make immutable
    override val ingredients: MutableList<IngredientModel?> = arrayOfNulls<IngredientModel?>(9).toMutableList(),
    private val ingredientToId: MutableMap<IngredientModel, Char> = mutableMapOf(),
    override var shape: RecipeCraftingModel.CraftingFormulaModel.Shaped.ShapeModel = ShapeModel(ingredients, ingredientToId),
) : RecipeCraftingModel.CraftingFormulaModel.Shaped {

    override fun assignIngredient(
        index: Int,
        ingredient: Ingredient,
    ) {
        assignIngredient(index, CustomIngredientModelImpl.loadFrom(ingredient))
    }

    override fun assignIngredient(
        index: Int,
        ingredient: IngredientModel,
    ) {
        if (index >= 0 && index < ingredients.size) {
            ingredients[index] = ingredient

            ingredientToId.clear()
            for ((index, ingredientState) in ingredients.distinct().withIndex()) {
                if (ingredientState != null) {
                    ingredientToId[ingredientState] = index.digitToChar()
                }
            }
        }
    }

    override fun unassignIngredient(index: Int) {
        if (index >= 0 && index < ingredients.size) {
            ingredients[index] = null
        }
    }

    override fun getIngredient(index: Int): IngredientModel? {
        if (index >= 0 && index < ingredients.size) {
            return ingredients[index]
        }
        return null
    }

    override fun clearIngredient(index: Int) {
        if (index >= 0 && index < ingredients.size) {
            ingredients[index] = null
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

        val shaped = ShapedCraftingFormulaImpl(mappedIngredients, completedShape)
        return Result.success(shaped)
    }

    class ShapeModel(
        val ingredients: List<IngredientModel?>,
        private val ingredientToId: Map<IngredientModel, Char>,
        override var symmetry: CraftingFormula.Shaped.ShapeSymmetry = ShapedCraftingFormulaImpl.ShapeSymmetryImpl(
            horizontal = false,
            vertical = false,
            rotate = false
        ),
        override var trim: Boolean = true,
    ) : RecipeCraftingModel.CraftingFormulaModel.Shaped.ShapeModel {

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

