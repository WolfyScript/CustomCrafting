package com.wolfyscript.customcrafting.recipes

import com.google.common.collect.Multimaps
import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap

class RecipeManagerCommon(val customCraftingCommon: CustomCraftingCommon) : RecipeManager {

    private val recipesByType = Multimaps.newListMultimap(Object2ReferenceOpenHashMap<RecipeType<*>, Collection<CustomRecipe>>()) { mutableListOf() }

    fun indexRecipes() {
        recipesByType.clear()
        for (recipe in customCraftingCommon.registries.customRecipes) {
            recipesByType.put(recipe.type, recipe)
        }
    }

    inline fun <reified T: CustomRecipe> getRecipeTyped(key: Key, type: RecipeType<T>): T? {
        val recipe = customCraftingCommon.registries.customRecipes[key] ?: return null
        if (recipe.type != type) {
            return null
        }
        return recipe as T
    }

    private fun <T: CustomRecipe> byType(type: RecipeType<T>): Collection<T> {
        return recipesByType.get(type) as Collection<T>
    }

    override fun evaluateCraftingRecipes(
        matrix: CraftingMatrixData,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeCrafting>? {
        return byType(RecipeTypes.crafting).map { it.evaluate(matrix, context) }.first { it != null }
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

    override fun disableRecipe(recipe: CustomRecipe) {
        TODO("Not yet implemented")
    }

    override fun enableRecipe(key: Key) {
        TODO("Not yet implemented")
    }

    override fun getRecipe(key: Key): CustomRecipe? {
        TODO("Not yet implemented")
    }

    override fun removeRecipe(key: Key) {
        TODO("Not yet implemented")
    }

    override fun updateRecipe(
        key: Key,
        recipe: CustomRecipe,
    ) {
        TODO("Not yet implemented")
    }

}