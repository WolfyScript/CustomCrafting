package com.wolfyscript.customcrafting.fabric.recipes.proxy

import com.wolfyscript.customcrafting.fabric.inject.CraftingCustomInputDataExt
import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.customcrafting.recipes.ShapedCraftingFormulaImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState
import com.wolfyscript.scafall.wrappers.utils.unwrap
import net.minecraft.core.HolderLookup
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import kotlin.collections.mapValues
import kotlin.random.Random

fun CraftingFormula.Shaped.toShapedRecipePattern(): ShapedRecipePattern {
    this as ShapedCraftingFormulaImpl
    val ingredients = this.mappedIngredients.mapValues { Ingredient.of(*it.value.choices.stacks.map { stack -> stack.create().unwrap().item }.toTypedArray()) }
    return ShapedRecipePattern.of(ingredients, shape.rows)
}

private fun RecipeReference<CustomRecipeCrafting>.matches(recipeInput: CraftingInput, level: Level): Boolean {
    val recipe = value ?: return false
    if (recipeInput !is CraftingCustomInputDataExt) return false
    val data = recipeInput.customInput ?: return false
    val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)

    val result = recipe.evaluate(data, context) ?: return false
    recipeInput.resultInfo = RecipeEvaluationResultImpl(this, result)
    return true
}

private fun RecipeReference<CustomRecipeCrafting>.assemble(recipeInput: CraftingInput, provider: HolderLookup.Provider): ItemStack? {
    val recipe = value ?: return null
    if (recipeInput !is CraftingCustomInputDataExt) return null
    val resultInfo = recipeInput.resultInfo ?: return null
    val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)

    val stack = recipe.result.compute(resultInfo, context, Random)
    return stack.unwrap()
}

class CustomRecipeShapedProxy(val customRecipe: RecipeReference<CustomRecipeCrafting>) : ShapedRecipe(
    "", // TODO
    CraftingBookCategory.MISC, // TODO
    (customRecipe.value!!.formula as CraftingFormula.Shaped).toShapedRecipePattern(),
    (customRecipe.value!!).result.choices.stacks.first().create().unwrap()
), ProxyRecipe {

    override fun matches(
        recipeInput: CraftingInput,
        level: Level,
    ): Boolean {
        return customRecipe.matches(recipeInput, level)
    }

    override fun assemble(
        recipeInput: CraftingInput,
        provider: HolderLookup.Provider,
    ): ItemStack? {
        return customRecipe.assemble(recipeInput, provider)
    }

}

class CustomRecipeShapelessProxy(val customRecipe: RecipeReference<CustomRecipeCrafting>) : ShapelessRecipe(
    "", // TODO
    CraftingBookCategory.MISC, // TODO
    (customRecipe.value!!).result.choices.stacks.first().create().unwrap(),
    ((customRecipe.value!!).formula as CraftingFormula.Shapeless).ingredients.map { Ingredient.of(*it.choices.stacks.map { stack -> stack.create().unwrap().item }.toTypedArray()) }
), ProxyRecipe {

    override fun matches(
        recipeInput: CraftingInput,
        level: Level,
    ): Boolean {
        return customRecipe.matches(recipeInput, level)
    }

    override fun assemble(
        recipeInput: CraftingInput,
        provider: HolderLookup.Provider,
    ): ItemStack? {
        return customRecipe.assemble(recipeInput, provider)
    }

}

fun RecipeReference<CustomRecipeCrafting>.toVanilla(): CraftingRecipe? {
    val recipe = this.value ?: return null
    val formula = recipe.formula

    return when (formula) {
        is CraftingFormula.Shaped -> CustomRecipeShapedProxy(this)
        is CraftingFormula.Shapeless -> CustomRecipeShapelessProxy(this)
        else -> null
    }
}
