package com.wolfyscript.customcrafting.fabric.recipes.proxy

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSingleSlotCustomExt
import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeCooking
import com.wolfyscript.customcrafting.core.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.core.recipes.RecipeReference
import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.core.recipes.state.EvaluationContextState
import com.wolfyscript.scafall.wrappers.minecraft.unwrap
import net.minecraft.core.HolderLookup
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import kotlin.random.Random

fun RecipeReference<CustomRecipeCooking>.matches(input: SingleRecipeInput, level: Level): Boolean {
    if (CustomCraftingProvider.get().server!!.recipeManager.isRecipeDisabled(key)) { return false }
    val recipe = value ?: return false
    if (input !is RecipeInputSingleSlotCustomExt) return false
    val data = input.customInput ?: return false
    val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)

    val result = recipe.evaluate(data, context) ?: return false
    input.resultInfo = RecipeEvaluationResultImpl(this, result)
    return true
}

fun RecipeReference<CustomRecipeCooking>.assemble(
    input: SingleRecipeInput,
    provider: HolderLookup.Provider,
): ItemStack? {
    if (CustomCraftingProvider.get().server!!.recipeManager.isRecipeDisabled(key)) { return ItemStack.EMPTY }
    val recipe = value ?: return null
    if (input !is RecipeInputSingleSlotCustomExt) return null
    val resultInfo = input.resultInfo ?: return null
    val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)

    return recipe.result.compute(resultInfo, context, Random).unwrap()
}

class CustomSmeltingRecipeProxy(override val customRecipe: RecipeReference<CustomRecipeCooking>) : SmeltingRecipe(
    "",
    CookingBookCategory.MISC,
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().create().unwrap(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
), ProxyRecipe {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack {
        return customRecipe.assemble(singleRecipeInput, provider) ?: ItemStack.EMPTY
    }

}

class CustomBlastingRecipeProxy(override val customRecipe: RecipeReference<CustomRecipeCooking>) : BlastingRecipe(
    "",
    CookingBookCategory.MISC,
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().create().unwrap(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
), ProxyRecipe {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack {
        return customRecipe.assemble(singleRecipeInput, provider) ?: ItemStack.EMPTY
    }
}

class CustomSmokingRecipeProxy(override val customRecipe: RecipeReference<CustomRecipeCooking>) : SmokingRecipe(
    "",
    CookingBookCategory.MISC,
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().create().unwrap(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
), ProxyRecipe {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack {
        return customRecipe.assemble(singleRecipeInput, provider) ?: ItemStack.EMPTY
    }
}

class CustomCampfireRecipeProxy(override val customRecipe: RecipeReference<CustomRecipeCooking>) : CampfireCookingRecipe(
    "",
    CookingBookCategory.MISC,
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().create().unwrap(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
), ProxyRecipe {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack {
        return customRecipe.assemble(singleRecipeInput, provider) ?: ItemStack.EMPTY
    }

}