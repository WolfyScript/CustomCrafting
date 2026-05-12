package com.wolfyscript.customcrafting.fabric.recipes.proxy

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputCraftingCustomExt
import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe
import com.wolfyscript.customcrafting.fabric.inject.getRecipeResultCachedRandom
import com.wolfyscript.customcrafting.core.recipes.CraftingFormula
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.core.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.core.recipes.RecipeReference
import com.wolfyscript.customcrafting.core.recipes.ShapedCraftingFormulaImpl
import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.core.recipes.state.EvaluationContextState
import com.wolfyscript.customcrafting.core.util.toTemplate
import com.wolfyscript.scafall.wrappers.minecraft.unwrap
import net.minecraft.server.level.ServerPlayer
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
    if (CustomCraftingProvider.get().server!!.recipeManager.isRecipeDisabled(key)) { return false }
    val recipe = value ?: return false
    if (recipeInput !is RecipeInputCraftingCustomExt) { return false }
    val data = recipeInput.customInput ?: return false
    val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)

    val result = recipe.evaluate(data, context) ?: return false
    recipeInput.resultInfo = RecipeEvaluationResultImpl(this, result)
    return true
}

private fun RecipeReference<CustomRecipeCrafting>.assemble(recipeInput: CraftingInput): ItemStack {
    if (CustomCraftingProvider.get().server!!.recipeManager.isRecipeDisabled(key)) { return ItemStack.EMPTY }
    val recipe = value ?: return ItemStack.EMPTY
    if (recipeInput !is RecipeInputCraftingCustomExt) { return ItemStack.EMPTY }
    val resultInfo = recipeInput.resultInfo ?: return ItemStack.EMPTY
    val context = EvaluationContextState.current ?: return ItemStack.EMPTY
    val random = (context.player?.unwrap() as? ServerPlayer)?.getRecipeResultCachedRandom(key, recipe.result.alwaysKeepPrevious) ?: Random

    val stack = recipe.result.compute(resultInfo, context, random)
    return stack.unwrap()
}

class CustomRecipeShapedProxy(override val customRecipe: RecipeReference<CustomRecipeCrafting>) : ShapedRecipe(
    Recipe.CommonInfo(false),
    CraftingRecipe.CraftingBookInfo(
        CraftingBookCategory.MISC,
        customRecipe.value!!.group
    ),
    (customRecipe.value!!.formula as CraftingFormula.Shaped).toShapedRecipePattern(),
    (customRecipe.value!!).result.choices.stacks.first().toTemplate()
), ProxyRecipe {

    override fun matches(
        recipeInput: CraftingInput,
        level: Level,
    ): Boolean {
        return customRecipe.matches(recipeInput, level)
    }

    override fun assemble(recipeInput: CraftingInput): ItemStack {
        return customRecipe.assemble(recipeInput)
    }

}

class CustomRecipeShapelessProxy(override val customRecipe: RecipeReference<CustomRecipeCrafting>) : ShapelessRecipe(
    Recipe.CommonInfo(false),
    CraftingRecipe.CraftingBookInfo(
        CraftingBookCategory.MISC,
        customRecipe.value!!.group
    ),
    (customRecipe.value!!).result.choices.stacks.first().toTemplate(),
    ((customRecipe.value!!).formula as CraftingFormula.Shapeless).ingredients.map { Ingredient.of(*it.choices.stacks.map { stack -> stack.create().unwrap().item }.toTypedArray()) }
), ProxyRecipe {

    override fun matches(
        recipeInput: CraftingInput,
        level: Level,
    ): Boolean {
        return customRecipe.matches(recipeInput, level)
    }

    override fun assemble(recipeInput: CraftingInput): ItemStack {
        return customRecipe.assemble(recipeInput)
    }

}
