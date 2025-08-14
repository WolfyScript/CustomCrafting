package com.wolfyscript.customcrafting.fabric.recipes.proxy

import com.wolfyscript.customcrafting.fabric.inject.CookingCustomInputExt
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState
import com.wolfyscript.scafall.wrappers.utils.unwrap
import net.minecraft.core.HolderLookup
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import kotlin.random.Random

fun RecipeReference<CustomRecipeCooking>.matches(input: SingleRecipeInput, level: Level): Boolean {
    val recipe = value ?: return false
    if (input !is CookingCustomInputExt) return false
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
    val recipe = value ?: return null
    if (input !is CookingCustomInputExt) return null
    val resultInfo = input.resultInfo ?: return null
    val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)

    return recipe.result.compute(resultInfo, context, Random).unwrap()
}



class CustomSmeltingRecipeProxy(val customRecipe: RecipeReference<CustomRecipeCooking>) : SmeltingRecipe(
    "",
    CookingBookCategory.MISC,
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().create().unwrap(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
) {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack? {
        return customRecipe.assemble(singleRecipeInput, provider)
    }

}

class CustomBlastingRecipeProxy(val customRecipe: RecipeReference<CustomRecipeCooking>) : BlastingRecipe(
    "",
    CookingBookCategory.MISC,
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().create().unwrap(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
) {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack? {
        return customRecipe.assemble(singleRecipeInput, provider)
    }
}

class CustomSmokingRecipeProxy(val customRecipe: RecipeReference<CustomRecipeCooking>) : SmokingRecipe(
    "",
    CookingBookCategory.MISC,
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().create().unwrap(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
) {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack? {
        return customRecipe.assemble(singleRecipeInput, provider)
    }
}

class CustomCampfireRecipeProxy(val customRecipe: RecipeReference<CustomRecipeCooking>) : CampfireCookingRecipe(
    "",
    CookingBookCategory.MISC,
    Ingredient.of(*customRecipe.value!!.processing.source.choices.stacks.map { stack -> stack.create().unwrap().item }
        .toTypedArray()),
    customRecipe.value!!.result.choices.stacks.first().create().unwrap(),
    customRecipe.value!!.xp,
    customRecipe.value!!.processing.processingTime,
) {

    override fun matches(singleRecipeInput: SingleRecipeInput, level: Level): Boolean {
        return customRecipe.matches(singleRecipeInput, level)
    }

    override fun assemble(singleRecipeInput: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack? {
        return customRecipe.assemble(singleRecipeInput, provider)
    }

}