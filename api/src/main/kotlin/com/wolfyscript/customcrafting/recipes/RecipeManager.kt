package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface RecipeManager {

    fun evaluateCraftingRecipes(matrix: CraftingMatrixData, context: EvaluationContext): RecipeData<CustomRecipeCrafting>?

    fun evaluateCookingRecipes(input: ItemStack, context: EvaluationContext): RecipeData<CustomRecipeCooking>?

    fun evaluateRepairingRecipes(base: ItemStack, addition: ItemStack, context: EvaluationContext): RecipeData<CustomRecipeRepairing>?

    fun evaluateSmithingRecipes(left: ItemStack, right: ItemStack, context: EvaluationContext): RecipeData<CustomRecipeSmithing>?

    fun evaluateStonecuttingRecipes(input: ItemStack, context: EvaluationContext): RecipeData<CustomRecipeStonecutting>?

    fun evaluateGrindingRecipes(inputTop: ItemStack, inputBottom: ItemStack, context: EvaluationContext): RecipeData<CustomRecipeGrinding>?

    fun evaluateMixingRecipes(input: Array<ItemStack>, context: EvaluationContext): RecipeData<CustomRecipeMixing>? // TODO

    fun disableRecipe(recipe: CustomRecipe)

    fun enableRecipe(key: Key)

    val disabledRecipes: Set<Key>

    fun getRecipe(key: Key): CustomRecipe?

    fun removeRecipe(key: Key)

    fun updateRecipe(key: Key, recipe: CustomRecipe)

}