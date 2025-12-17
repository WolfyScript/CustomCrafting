package com.wolfyscript.customcrafting.editor.recipes

import com.wolfyscript.customcrafting.editor.IngredientStore
import com.wolfyscript.customcrafting.editor.RecipeState
import com.wolfyscript.customcrafting.editor.recipe_stores.RecipeCraftingState
import com.wolfyscript.customcrafting.editor.result.ResultState
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.CustomRecipeCraftingImpl
import com.wolfyscript.customcrafting.recipes.RecipeConditionsImpl
import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.customcrafting.recipes.RecipeTypes
import com.wolfyscript.customcrafting.recipes.ShapedCraftingFormulaImpl
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient

class RecipeCraftingStateFactory() : RecipeState.RecipeTypeSpecificState.Factory<CustomRecipeCrafting> {

    override val recipeType: RecipeType<CustomRecipeCrafting> by lazy { RecipeTypes.crafting.resolveOrThrow() }

    override fun edit(recipe: CustomRecipeCrafting): RecipeState.RecipeTypeSpecificState<CustomRecipeCrafting> {
        return RecipeCraftingStateImpl() // TODO: load recipe into state
    }

    override fun create(): RecipeState.RecipeTypeSpecificState<CustomRecipeCrafting> {
        return RecipeCraftingStateImpl()
    }

}


class RecipeCraftingStateImpl : RecipeCraftingState {

    override val result: ResultState = ResultStateImpl()
    override var formula: RecipeCraftingState.CraftingFormulaState<*> = ShapedCraftingFormula()
        private set

    override fun complete(common: RecipeState<CustomRecipeCrafting>): Result<CustomRecipeCrafting> {
        val completedFormula = formula.complete()
        if (completedFormula.isFailure) {
            return Result.failure(IllegalStateException("Failed to create crafting recipe formula: ${completedFormula.exceptionOrNull()?.message ?: "Unknown error" }"))
        }
        val completedResult = result.complete()
        if (completedResult.isFailure) {
            return Result.failure(IllegalStateException("Failed to create crafting recipe result: ${completedResult.exceptionOrNull()?.message ?: "Unknown error"}"))
        }

        val recipe = CustomRecipeCraftingImpl(
            priority = common.priority,
            conditions = common.condition?.complete()?.getOrNull() ?: RecipeConditionsImpl(),
            formula = completedFormula.getOrThrow(),
            result = completedResult.getOrThrow()
        )
        return Result.success(recipe)
    }

}

class ShapelessCraftingFormula : RecipeCraftingState.CraftingFormulaState.Shapeless {

    override val ingredients: MutableList<IngredientStore> = mutableListOf()

    override fun addIngredient(ingredient: Ingredient) {
        TODO("Not yet implemented")
    }

    override fun removeIngredient(index: Int) {
        TODO("Not yet implemented")
    }

    override fun complete(): Result<CraftingFormula.Shapeless> {
        TODO("Not yet implemented")
    }

}

class ShapedCraftingFormula : RecipeCraftingState.CraftingFormulaState.Shaped {

    override val ingredients: MutableList<IngredientStore> = mutableListOf()

    override fun assignIngredient(
        index: Int,
        ingredient: Ingredient,
    ) {
        if (index > 0 && index < ingredients.size) {
//                ingredients[index] = ingredient
        }
    }

    override fun clearIngredient(index: Int) {
        if (index > 0 && index < ingredients.size) {
            ingredients.removeAt(index)
        }
    }

    override var shape: RecipeCraftingState.CraftingFormulaState.Shaped.ShapeState = CraftingFormulaShapeState()

    override fun complete(): Result<CraftingFormula.Shaped> {
        TODO("Not yet implemented")
    }

}

class CraftingFormulaShapeState() : RecipeCraftingState.CraftingFormulaState.Shaped.ShapeState {

    override var symmetry: CraftingFormula.Shaped.ShapeSymmetry = ShapedCraftingFormulaImpl.ShapeSymmetryImpl(
        horizontal = false,
        vertical = false,
        rotate = false
    )

    override var trim: Boolean = true

    override fun complete(): Result<CraftingFormula.Shaped.Shape> {
        TODO("Not yet implemented")
    }
}
