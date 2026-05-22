package com.wolfyscript.customcrafting.fabric.recipes.proxy

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.recipe.CustomRecipeCooking
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.RecipeReference
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContextState
import com.wolfyscript.customcrafting.core.util.toTemplate
import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSingleSlotCustomExt
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import kotlin.random.Random

fun RecipeReference<CustomRecipeCooking>.matches(input: SingleRecipeInput, level: Level): Boolean {
    if (CustomCraftingProvider.get().server!!.recipeManager.isRecipeDisabled(key)) {
        return false
    }
    val recipe = value ?: return false
    if (input !is RecipeInputSingleSlotCustomExt) return false
    val data = input.customInput ?: return false
    val context = EvaluationContextState.current ?: EvaluationContext.of(null, null)

    val result = recipe.evaluate(data, context) ?: return false
    input.resultInfo = RecipeEvaluationResultImpl(this, result)
    return true
}

fun RecipeReference<CustomRecipeCooking>.assemble(
    input: SingleRecipeInput,
): ItemStack? {
    if (CustomCraftingProvider.get().server!!.recipeManager.isRecipeDisabled(key)) {
        return ItemStack.EMPTY
    }
    val recipe = value ?: return null
    if (input !is RecipeInputSingleSlotCustomExt) return null
    val resultInfo = input.resultInfo ?: return null
    val context = EvaluationContextState.current ?: EvaluationContext.of(null, null)

    return recipe.result.compute(resultInfo, context, Random).unwrap()
}

class CustomSmeltingRecipeProxy(override val customRecipe: RecipeReference<CustomRecipeCooking>) : SmeltingRecipe(
    Recipe.CommonInfo(false),
    CookingBookInfo(
        CookingBookCategory.MISC,
        customRecipe.value!!.group
    ),
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().toTemplate(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
), ProxyRecipe {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput): ItemStack {
        return customRecipe.assemble(singleRecipeInput) ?: ItemStack.EMPTY
    }

}

class CustomBlastingRecipeProxy(override val customRecipe: RecipeReference<CustomRecipeCooking>) : BlastingRecipe(
    Recipe.CommonInfo(false),
    CookingBookInfo(
        CookingBookCategory.MISC,
        customRecipe.value!!.group
    ),
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().toTemplate(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
), ProxyRecipe {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput): ItemStack {
        return customRecipe.assemble(singleRecipeInput) ?: ItemStack.EMPTY
    }
}

class CustomSmokingRecipeProxy(override val customRecipe: RecipeReference<CustomRecipeCooking>) : SmokingRecipe(
    Recipe.CommonInfo(false),
    CookingBookInfo(
        CookingBookCategory.MISC,
        customRecipe.value!!.group
    ),
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().toTemplate(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
), ProxyRecipe {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput): ItemStack {
        return customRecipe.assemble(singleRecipeInput) ?: ItemStack.EMPTY
    }
}

class CustomCampfireRecipeProxy(override val customRecipe: RecipeReference<CustomRecipeCooking>) :
    CampfireCookingRecipe(
        Recipe.CommonInfo(false),
        CookingBookInfo(
            CookingBookCategory.MISC,
            customRecipe.value!!.group
        ),
        Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack ->
            stack.create().unwrap().item
        }
            .toTypedArray()),
        customRecipe.value!!.result.choices.stacks.first().toTemplate(),
        customRecipe.value!!.xp,
        customRecipe.value!!.processing.processingTime,
    ), ProxyRecipe {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput): ItemStack {
        return customRecipe.assemble(singleRecipeInput) ?: ItemStack.EMPTY
    }

}