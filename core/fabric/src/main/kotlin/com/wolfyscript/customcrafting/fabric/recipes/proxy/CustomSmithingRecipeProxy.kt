package com.wolfyscript.customcrafting.fabric.recipes.proxy

import com.mojang.serialization.codecs.RecordCodecBuilder
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSmithingCustomExt
import com.wolfyscript.customcrafting.fabric.inject.getRecipeResultCachedRandom
import com.wolfyscript.customcrafting.core.recipe.CustomRecipeSmithing
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.RecipeReference
import com.wolfyscript.customcrafting.core.recipe.RecipeTypes
import com.wolfyscript.customcrafting.core.recipe.SmithingUtils
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.getRecipeTyped
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContextState
import com.wolfyscript.customcrafting.core.util.toMc
import com.wolfyscript.customcrafting.core.util.toMcDisplay
import com.wolfyscript.scafall.identifier.toScafall
import com.wolfyscript.scafall.wrappers.minecraft.unwrap
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.*
import net.minecraft.world.item.crafting.display.RecipeDisplay
import net.minecraft.world.item.crafting.display.SlotDisplay
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay
import net.minecraft.world.level.Level
import java.util.*

class CustomSmithingRecipeProxy(override val customRecipe: RecipeReference<CustomRecipeSmithing>) : SmithingRecipe, ProxyRecipe {

    companion object {

        @JvmField
        val SERIALIZER = RecipeSerializer<CustomSmithingRecipeProxy>(
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Identifier.CODEC.fieldOf("customRecipe").forGetter { it.customRecipe.key.toMc() }
                ).apply(instance) { recipeFromLocation(it) }
            },
            StreamCodec.composite(
                Identifier.STREAM_CODEC,
                { recipe -> recipe.customRecipe.key.toMc() },
                { recipeFromLocation(it)}
            )
        )

        private fun recipeFromLocation(recipeId: Identifier): CustomSmithingRecipeProxy {
            val key = recipeId.toScafall()
            val recipe = CustomCraftingProvider.get().server!!.recipeManager.getRecipeTyped(key, RecipeTypes.smithing.resolveOrThrow())
                ?: error("Recipe not found: $recipeId")
            return CustomSmithingRecipeProxy(recipe)
        }
    }

    init {
        if (customRecipe.value == null) throw IllegalArgumentException("Cannot create a SmithingRecipeProxy for a null recipe")
    }

    private val vanillaAdditionIngredient = Optional.ofNullable(customRecipe.value!!.addition?.toMc())
    private val vanillaBaseIngredient: Ingredient = customRecipe.value!!.base.toMc()
    private val vanillaTemplateIngredient = Optional.ofNullable(customRecipe.value!!.template?.toMc())

    private val placementInfo: PlacementInfo by lazy {
        PlacementInfo.createFromOptionals(
            listOf(
                this.vanillaTemplateIngredient,
                customRecipe.value?.base?.toMc()?.let { Optional.of(it) } ?: Optional.empty(),
                this.vanillaAdditionIngredient
            )
        )
    }

    override fun templateIngredient(): Optional<Ingredient> {
        return vanillaTemplateIngredient
    }

    override fun baseIngredient(): Ingredient {
        return vanillaBaseIngredient
    }

    override fun additionIngredient(): Optional<Ingredient> {
        return vanillaAdditionIngredient
    }

    override fun matches(smithingRecipeInput: SmithingRecipeInput, level: Level): Boolean {
        if (CustomCraftingProvider.get().server!!.recipeManager.isRecipeDisabled(customRecipe.key)) { return false }
        val recipe = customRecipe.value ?: return false
        if (smithingRecipeInput !is RecipeInputSmithingCustomExt) return false
        val customInput = smithingRecipeInput.customInput ?: return false
        val context = EvaluationContextState.current ?: EvaluationContext.of(null, null)

        val result = recipe.evaluate(customInput, context) ?: return false
        smithingRecipeInput.resultInfo = RecipeEvaluationResult.of(customRecipe, result)
        return true
    }

    override fun assemble(
        input: SmithingRecipeInput,
    ): ItemStack {
        if (CustomCraftingProvider.get().server!!.recipeManager.isRecipeDisabled(customRecipe.key)) { return ItemStack.EMPTY }
        val recipe = customRecipe.value ?: return ItemStack.EMPTY
        if (input !is RecipeInputSmithingCustomExt) return ItemStack.EMPTY
        val resultInfo = input.resultInfo ?: return ItemStack.EMPTY
        val baseStack = input.customInput?.base ?: return ItemStack.EMPTY
        val context = EvaluationContextState.current ?: EvaluationContext.of(null, null)
        val random = (context.player?.unwrap() as? ServerPlayer)?.getRecipeResultCachedRandom(customRecipe.key, recipe.result.alwaysKeepPrevious) ?: return ItemStack.EMPTY

        val stack = recipe.result.compute(resultInfo, context, random)
        SmithingUtils.copyDataComponentsTo(baseStack, stack, recipe.copyOptions!!)
        return stack.unwrap()
    }

    override fun showNotification(): Boolean = false

    override fun group(): String {
        return customRecipe.value?.group ?: ""
    }

    override fun placementInfo(): PlacementInfo = placementInfo

    override fun display(): List<RecipeDisplay> {
        val recipe = customRecipe.value ?: return listOf()
        return listOf(
            SmithingRecipeDisplay(
                recipe.template.toMcDisplay(),
                recipe.base.toMcDisplay(),
                recipe.addition.toMcDisplay(),
                recipe.result.toMcDisplay(),
                SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)
            )
        )
    }

    override fun getSerializer(): RecipeSerializer<CustomSmithingRecipeProxy> {
        return SERIALIZER
    }



}
