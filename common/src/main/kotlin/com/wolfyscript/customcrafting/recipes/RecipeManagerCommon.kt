package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class RecipeManagerCommon : RecipeManager {

    override fun evaluateCraftingRecipes(
        matrix: CraftingMatrixData,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeCrafting>? {
        TODO("Not yet implemented")
    }

    override fun evaluateCookingRecipes(
        input: ItemStack,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeCooking>? {
        TODO("Not yet implemented")
    }

    override fun evaluateRepairingRecipes(
        base: ItemStack,
        addition: ItemStack,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeRepairing>? {
        TODO("Not yet implemented")
    }

    override fun evaluateSmithingRecipes(
        left: ItemStack,
        right: ItemStack,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeSmithing>? {
        TODO("Not yet implemented")
    }

    override fun evaluateStonecuttingRecipes(
        input: ItemStack,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeStonecutting>? {
        TODO("Not yet implemented")
    }

    override fun evaluateGrindingRecipes(
        inputTop: ItemStack,
        inputBottom: ItemStack,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeGrinding>? {
        TODO("Not yet implemented")
    }

    override fun evaluateMixingRecipes(
        input: Array<ItemStack>,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeMixing>? {
        TODO("Not yet implemented")
    }

    override fun disableRecipe(recipe: CustomRecipe<*>) {
        TODO("Not yet implemented")
    }

    override fun enableRecipe(key: Key) {
        TODO("Not yet implemented")
    }

    override fun getRecipe(key: Key): CustomRecipe<*>? {
        TODO("Not yet implemented")
    }

    override fun removeRecipe(key: Key) {
        TODO("Not yet implemented")
    }

    override fun updateRecipe(
        key: Key,
        recipe: CustomRecipe<*>,
    ) {
        TODO("Not yet implemented")
    }

}