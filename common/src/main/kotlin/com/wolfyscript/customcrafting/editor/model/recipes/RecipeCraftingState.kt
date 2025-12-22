package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.editor.model.recipes.RecipeState
import com.wolfyscript.customcrafting.editor.model.recipes.result.ResultState
import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import kotlin.collections.get

class RecipeCraftingStateFactory() : RecipeState.RecipeTypeSpecificState.Factory<CustomRecipeCrafting> {

    override val recipeType: RecipeType<CustomRecipeCrafting> by lazy { RecipeTypes.crafting.resolveOrThrow() }

    override fun edit(recipe: CustomRecipeCrafting): RecipeState.RecipeTypeSpecificState<CustomRecipeCrafting> {
        return RecipeCraftingStateImpl() // TODO: load recipe into state
    }

    override fun create(): RecipeState.RecipeTypeSpecificState<CustomRecipeCrafting> {
        return RecipeCraftingStateImpl()
    }

}

class IngredientCollectionStateImpl : RecipeCraftingState.IngredientCollection {

    override val ingredients: MutableList<IngredientState> = mutableListOf()

    override fun addNew() {
        add(CustomIngredientStateImpl())
    }

    override fun add(ingredient: IngredientState) {
        if (ingredients.size < 9) {
            ingredients.add(ingredient)
        }
    }

    override fun remove(index: Int) {
        ingredients.removeAt(index)
    }

}

class RecipeCraftingStateImpl : RecipeCraftingState {

    override val ingredientCollection: RecipeCraftingState.IngredientCollection = IngredientCollectionStateImpl()
    override val result: ResultState = ResultStateImpl()
    override var formula: RecipeCraftingState.CraftingFormulaState<*> = ShapedCraftingFormulaState()
        private set

    override fun setFormulaType(type: Class<out CraftingFormula>) {
        val previousFormula = formula
        formula = when (type) {
            CraftingFormula.Shaped::class.java -> ShapedCraftingFormulaState()
            CraftingFormula.Shapeless::class.java -> ShapelessCraftingFormulaState()
            else -> ShapedCraftingFormulaState()
        }
        // TODO: copy properties like ingredients to not reset them
    }

    override fun complete(common: RecipeState<CustomRecipeCrafting>): Result<CustomRecipeCrafting> {
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

class ShapelessCraftingFormulaState : RecipeCraftingState.CraftingFormulaState.Shapeless {

    override val ingredients: MutableList<IngredientState> = mutableListOf()

    override fun addIngredient(ingredient: Ingredient) {
        if (ingredients.size < 9) {
            ingredients.add(CustomIngredientStateImpl.loadFrom(ingredient))
        }
    }

    override fun addIngredient(ingredient: IngredientState) {
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

class ShapedCraftingFormulaState : RecipeCraftingState.CraftingFormulaState.Shaped {

    override val ingredients: MutableList<IngredientState?> = arrayOfNulls<IngredientState?>(9).toMutableList()
    override var shape: RecipeCraftingState.CraftingFormulaState.Shaped.ShapeState = ShapeState()
    private val ingredientToId = mutableMapOf<IngredientState, Char>()

    override fun assignIngredient(
        index: Int,
        ingredient: Ingredient,
    ) {
        if (index > 0 && index < ingredients.size) {
            val state = CustomIngredientStateImpl.loadFrom(ingredient)
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

    inner class ShapeState() : RecipeCraftingState.CraftingFormulaState.Shaped.ShapeState {

        override var symmetry: CraftingFormula.Shaped.ShapeSymmetry = ShapedCraftingFormulaImpl.ShapeSymmetryImpl(
            horizontal = false,
            vertical = false,
            rotate = false
        )

        override var trim: Boolean = true

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

