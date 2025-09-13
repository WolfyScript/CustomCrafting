package com.wolfyscript.customcrafting.fabric.recipes.proxy

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSmithingCustomExt
import com.wolfyscript.customcrafting.fabric.inject.getRecipeResultCachedRandom
import com.wolfyscript.customcrafting.recipes.CustomRecipeSmithing
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl
import com.wolfyscript.customcrafting.recipes.RecipeReference
import com.wolfyscript.customcrafting.recipes.SmithingUtils
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResultImpl
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState
import com.wolfyscript.customcrafting.util.toMc
import com.wolfyscript.customcrafting.util.toMcDisplay
import com.wolfyscript.scafall.identifier.toScafall
import com.wolfyscript.scafall.wrappers.unwrap
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.*
import net.minecraft.world.item.crafting.display.RecipeDisplay
import net.minecraft.world.item.crafting.display.SlotDisplay
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay
import net.minecraft.world.level.Level
import java.util.*

class CustomSmithingRecipeProxy(val customRecipe: RecipeReference<CustomRecipeSmithing>) : SmithingRecipe, ProxyRecipe {

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
        val recipe = customRecipe.value ?: return false
        if (smithingRecipeInput !is RecipeInputSmithingCustomExt) return false
        val customInput = smithingRecipeInput.customInput ?: return false
        val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)

        val result = recipe.evaluate(customInput, context) ?: return false
        smithingRecipeInput.resultInfo = RecipeEvaluationResultImpl(customRecipe, result)
        return true
    }

    override fun assemble(
        input: SmithingRecipeInput,
        registries: HolderLookup.Provider,
    ): ItemStack {
        val recipe = customRecipe.value ?: return ItemStack.EMPTY
        if (input !is RecipeInputSmithingCustomExt) return ItemStack.EMPTY
        val resultInfo = input.resultInfo ?: return ItemStack.EMPTY
        val baseStack = input.customInput?.base ?: return ItemStack.EMPTY
        val context = EvaluationContextState.current ?: EvaluationContextImpl(null, null)
        val random = (context.player?.unwrap() as? ServerPlayer)?.getRecipeResultCachedRandom(customRecipe.key, recipe.result.alwaysKeepPrevious) ?: return ItemStack.EMPTY

        val stack = recipe.result.compute(resultInfo, context, random)
        SmithingUtils.copyDataComponentsTo(baseStack, stack, recipe.copyOptions!!)
        return stack.unwrap()
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
        return BuiltInRegistries.RECIPE_SERIALIZER.get(ResourceLocation.fromNamespaceAndPath("customcrafting", "smithing")).get().value() as RecipeSerializer<CustomSmithingRecipeProxy>
    }

    class Serializer : RecipeSerializer<CustomSmithingRecipeProxy> {

        companion object {
            val CODEC: MapCodec<CustomSmithingRecipeProxy> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    ResourceLocation.CODEC.fieldOf("customRecipe").forGetter { it.customRecipe.key.toMc() }
                ).apply(instance) { recipeFromLocation(it) }
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, CustomSmithingRecipeProxy> = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                { recipe -> recipe.customRecipe.key.toMc() },
                { recipeFromLocation(it)}
            )

            private fun recipeFromLocation(recipeId: ResourceLocation): CustomSmithingRecipeProxy? {
                val key = recipeId.toScafall()
                val recipe = CustomCraftingProvider.get().recipeManager.getRecipe(key)
                if (recipe !is CustomRecipeSmithing) return null
                val ref = RecipeReference.of(key, recipe)
                return CustomSmithingRecipeProxy(ref)
            }
        }

        override fun codec(): MapCodec<CustomSmithingRecipeProxy> {
            return CODEC
        }

        @Deprecated("Deprecated in Java. Most likely since recipes are no longer send to clients")
        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, CustomSmithingRecipeProxy> {
            return STREAM_CODEC
        }

    }

}
