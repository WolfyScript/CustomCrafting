package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.wrappers.utils.unwrap
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.level.block.entity.BlockEntityType

class CustomRecipeCookingImpl(
    override val processing: CustomRecipeCooking.WorkstationProcessing,
    override val result: RecipeResult,
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val xp: Float,
) : CustomRecipeCooking {

    override fun evaluate(
        input: RecipeInput.CookingRecipeInput,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeCooking>? {
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
            input: RecipeInput.CookingRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeData<CustomRecipeCooking>? {
            val (pos, level) = context.location?.unwrap() ?: return null
            val type = ScafallProvider.get().server.minecraftServer.getLevel(level)
                ?.getBlockEntity(BlockPos(Vec3i(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())))?.let {
                    it.type
                }
            if (type == null || type != BlockEntityType.FURNACE) {
                return null
            }

            val result = source.match(input.source, true)
            if (result == null) {
                return null
            }
            return RecipeDataImpl(recipe, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

        override fun toString(): String {
            return "smelting in $processingTime ticks from $source"
        }

    }

    class WorkstationProcessingBlasting(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Blasting {

        override fun evaluate(
            input: RecipeInput.CookingRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeData<CustomRecipeCooking>? {
            val (pos, level) = context.location?.unwrap() ?: return null
            val type = ScafallProvider.get().server.minecraftServer.getLevel(level)
                        ?.getBlockEntity(BlockPos(Vec3i(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())))?.type
            if (type == null || type != BlockEntityType.BLAST_FURNACE) {
                return null
            }


            val result = source.match(input.source, true)
            if (result == null) {
                return null
            }
            return RecipeDataImpl(recipe, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

        override fun toString(): String {
            return "blasting in $processingTime ticks from $source"
        }

    }

    class WorkstationProcessingSmoking(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Smoking {

        override fun evaluate(
            input: RecipeInput.CookingRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeData<CustomRecipeCooking>? {
            val (pos, level) = context.location?.unwrap() ?: return null
            val type = ScafallProvider.get().server.minecraftServer.getLevel(level)
                        ?.getBlockEntity(BlockPos(Vec3i(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())))?.type
            if (type == null || type != BlockEntityType.SMOKER) {
                return null
            }

            val result = source.match(input.source, true)
            if (result == null) {
                return null
            }
            return RecipeDataImpl(recipe, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

        override fun toString(): String {
            return "smoking in $processingTime ticks from $source"
        }

    }

    class WorkstationProcessingCampfire(
        override val soulCampfire: Boolean,
        override val normalCampfire: Boolean,
        override val processingTime: Int,
        override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Campfire {

        override fun evaluate(
            input: RecipeInput.CookingRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeData<CustomRecipeCooking>? {
            val (pos, level) = context.location?.unwrap() ?: return null
            val type = ScafallProvider.get().server.minecraftServer.getLevel(level)
                        ?.getBlockEntity(BlockPos(Vec3i(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())))?.type
            if (type == null || type != BlockEntityType.CAMPFIRE) {
                return null
            }

            val result = source.match(input.source, true)
            if (result == null) {
                return null
            }
            // TODO: Get proper slot in campfire. Perhaps through the context?
            return RecipeDataImpl(recipe, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

        override fun toString(): String {
            return "campfire in $processingTime ticks from $source on soul: $soulCampfire or normal: $normalCampfire"
        }

    }

}