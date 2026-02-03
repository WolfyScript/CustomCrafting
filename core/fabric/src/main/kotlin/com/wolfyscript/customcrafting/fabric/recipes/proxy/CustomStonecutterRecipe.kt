package com.wolfyscript.customcrafting.fabric.recipes.proxy

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSingleSlotCustomExt
import com.wolfyscript.customcrafting.fabric.inject.getRecipeResultCachedRandom
import com.wolfyscript.customcrafting.recipes.CustomRecipeStonecutting
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState
import com.wolfyscript.customcrafting.core.util.toMc
import com.wolfyscript.customcrafting.core.util.toMcDisplay
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.unwrap
import net.minecraft.core.HolderLookup
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.item.crafting.StonecutterRecipe
import net.minecraft.world.item.crafting.display.RecipeDisplay
import net.minecraft.world.item.crafting.display.SlotDisplay
import net.minecraft.world.item.crafting.display.StonecutterRecipeDisplay
import net.minecraft.world.level.Level

fun RecipeReference<CustomRecipeStonecutting>.matches(input: SingleRecipeInput, level: Level): Boolean {
    if (CustomCraftingProvider.get().server!!.recipeManager.isRecipeDisabled(key)) { return false }
    val recipe = value ?: return false
    input as RecipeInputSingleSlotCustomExt
    val data = input.customInput ?: return false
    val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)

    val result = recipe.evaluate(data, context) ?: return false
    input.resultInfo = RecipeEvaluationResultImpl(this, result)
    return true
}

fun RecipeReference<CustomRecipeStonecutting>.assemble(
    input: SingleRecipeInput,
    provider: HolderLookup.Provider,
): ItemStack {
    if (CustomCraftingProvider.get().server!!.recipeManager.isRecipeDisabled(key)) { return ItemStack.EMPTY }
    val recipe = value ?: return ItemStack.EMPTY
    input as RecipeInputSingleSlotCustomExt
    val resultInfo = input.resultInfo ?: return ItemStack.EMPTY
    val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)
    val random = (context.player?.unwrap() as? ServerPlayer)?.getRecipeResultCachedRandom(key, recipe.result.alwaysKeepPrevious) ?: return ItemStack.EMPTY

    return recipe.result.compute(resultInfo, context, random).unwrap()
}

class CustomStonecutterRecipeProxy : StonecutterRecipe, ProxyRecipe {

    override val customRecipe: RecipeReference<CustomRecipeStonecutting>
    val split: Boolean

    constructor(recipe: RecipeReference<CustomRecipeStonecutting>) : super(
        "",
        recipe.value!!.source.toMc(),
        recipe.value!!.result.choices.stacks.first().create().unwrap()
    ) {
        this.customRecipe = recipe
        split = false
    }

    internal constructor(recipe: RecipeReference<CustomRecipeStonecutting>, result: ItemStack) : super(
        "",
        recipe.value!!.source.toMc(),
        result
    ) {
        this.customRecipe = recipe
        split = true
    }

    override fun display(): List<RecipeDisplay> {
        return listOf(
            StonecutterRecipeDisplay(
                customRecipe.value?.source.toMcDisplay(),
                this.resultDisplay(),
                SlotDisplay.ItemSlotDisplay(Items.STONECUTTER)
            )
        )
    }

    override fun resultDisplay(): SlotDisplay {
        if (split) {
            return super.resultDisplay()
        }
        return customRecipe.value?.result?.toMcDisplay() ?: SlotDisplay.Empty.INSTANCE
    }

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(input: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack {
        if (split) {
            val recipeVal = customRecipe.value ?: return ItemStack.EMPTY
            input as RecipeInputSingleSlotCustomExt
            val resultInfo = input.resultInfo ?: return ItemStack.EMPTY
            val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)
            val stack = result().snapshot().createStack()
            recipeVal.result.modifier.modify(stack, resultInfo, context)
            return stack.unwrap()
        }
        return customRecipe.assemble(input, provider)
    }

}