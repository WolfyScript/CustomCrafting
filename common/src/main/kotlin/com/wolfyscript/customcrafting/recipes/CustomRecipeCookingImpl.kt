package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.DefaultDataImpl
import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.wrappers.utils.unwrap
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType

class CustomRecipeCookingImpl(
    override val processing: CustomRecipeCooking.WorkstationProcessing,
    override val result: RecipeResult,
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val xp: Float,
) : CustomRecipeCooking {

    override fun evaluate(
        input: RecipeInput.SingleSlotRecipeInput,
        context: EvaluationContext,
    ): RecipeEvaluationResult.Data? {
        if (!conditions.areSatisfied(context)) {
            return null
        }
        return processing.evaluate(input, this, context)
    }

    override fun toString(): String {
        return "Recipe Cooking ($priority) $processing to $result, giving ${xp}xp, if $conditions"
    }

    class WorkstationProcessingSmelting(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Smelting {

        override fun evaluate(
            input: RecipeInput.SingleSlotRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeEvaluationResult.Data? {
            if (context.blockEntity?.unwrap()?.type != BlockEntityType.FURNACE) {
                return null
            }

            val result = source.match(input.source) ?: return null
            return DefaultDataImpl(arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

        override fun toString(): String {
            return "smelting in $processingTime ticks from $source"
        }

    }

    class WorkstationProcessingBlasting(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Blasting {

        override fun evaluate(
            input: RecipeInput.SingleSlotRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeEvaluationResult.Data? {
            if (context.blockEntity?.unwrap()?.type != BlockEntityType.BLAST_FURNACE) {
                return null
            }

            val result = source.match(input.source) ?: return null
            return DefaultDataImpl(arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

        override fun toString(): String {
            return "blasting in $processingTime ticks from $source"
        }

    }

    class WorkstationProcessingSmoking(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Smoking {

        override fun evaluate(
            input: RecipeInput.SingleSlotRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeEvaluationResult.Data? {
            if (context.blockEntity?.unwrap()?.type != BlockEntityType.SMOKER) {
                return null
            }

            val result = source.match(input.source) ?: return null
            return DefaultDataImpl(arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

        override fun toString(): String {
            return "smoking in $processingTime ticks from $source"
        }

    }

    class WorkstationProcessingCampfire(
        override val soulCampfire: Boolean = true,
        override val normalCampfire: Boolean = true,
        override val processingTime: Int,
        override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Campfire {

        override fun evaluate(
            input: RecipeInput.SingleSlotRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeEvaluationResult.Data? {
            val blockState = context.blockEntity?.unwrap()?.blockState ?: return null

            val soul = blockState.block == Blocks.SOUL_CAMPFIRE
            val normal = blockState.block == Blocks.CAMPFIRE
            if (!normal && !soul || !soulCampfire && soul || !normalCampfire && normal) {
                return null
            }

            val result = source.match(input.source) ?: return null
            // TODO: Get proper slot in campfire. Perhaps through the context?
            return DefaultDataImpl(arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

        override fun toString(): String {
            return "campfire in $processingTime ticks from $source on soul: $soulCampfire or normal: $normalCampfire"
        }

    }

}