package com.wolfyscript.customcrafting.fabric.recipes.proxy

import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSingleSlotCustomExt
import com.wolfyscript.customcrafting.fabric.inject.RecipesState
import com.wolfyscript.customcrafting.fabric.inject.getRecipeRandom
import com.wolfyscript.customcrafting.recipes.CustomRecipeStonecutting
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState
import com.wolfyscript.customcrafting.util.toMc
import com.wolfyscript.customcrafting.util.toMcDisplay
import com.wolfyscript.scafall.wrappers.utils.snapshot
import com.wolfyscript.scafall.wrappers.utils.unwrap
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
import kotlin.random.Random

fun RecipeReference<CustomRecipeStonecutting>.matches(input: SingleRecipeInput, level: Level): Boolean {
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
    val recipe = value ?: return ItemStack.EMPTY
    input as RecipeInputSingleSlotCustomExt
    val resultInfo = input.resultInfo ?: return ItemStack.EMPTY
    val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)
    val random = (context.player?.unwrap() as? ServerPlayer)?.getRecipeRandom(RecipesState.stonecutting) ?: return ItemStack.EMPTY

    return recipe.result.compute(resultInfo, context, random).unwrap()
}

class CustomStonecutterRecipeProxy : StonecutterRecipe, ProxyRecipe {

    val recipe: RecipeReference<CustomRecipeStonecutting>
    val split: Boolean

    constructor(recipe: RecipeReference<CustomRecipeStonecutting>) : super(
        "",
        recipe.value!!.source.toMc(),
        recipe.value!!.result.choices.stacks.first().create().unwrap()
    ) {
        this.recipe = recipe
        split = false
    }

    internal constructor(recipe: RecipeReference<CustomRecipeStonecutting>, result: ItemStack) : super(
        "",
        recipe.value!!.source.toMc(),
        result
    ) {
        this.recipe = recipe
        split = true
    }

    override fun display(): List<RecipeDisplay> {
        return listOf(
            StonecutterRecipeDisplay(
                recipe.value?.source.toMcDisplay(),
                this.resultDisplay(),
                SlotDisplay.ItemSlotDisplay(Items.STONECUTTER)
            )
        )
    }

    override fun resultDisplay(): SlotDisplay {
        if (split) {
            return super.resultDisplay()
        }
        return recipe.value?.result?.toMcDisplay() ?: SlotDisplay.Empty.INSTANCE
    }

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return recipe.matches(singleRecipeInput, level)
    }

    override fun assemble(input: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack {
        if (split) {
            val recipeVal = recipe.value ?: return ItemStack.EMPTY
            input as RecipeInputSingleSlotCustomExt
            val resultInfo = input.resultInfo ?: return ItemStack.EMPTY
            val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)
            val stack = result().snapshot().createStack()
            recipeVal.result.modifier.modify(resultInfo, stack, context)
            return stack.unwrap()
        }
        return recipe.assemble(input, provider)
    }

}