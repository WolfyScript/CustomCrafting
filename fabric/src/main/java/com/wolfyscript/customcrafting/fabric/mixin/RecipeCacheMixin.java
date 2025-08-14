package com.wolfyscript.customcrafting.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.fabric.inject.CraftingCustomInputDataExt;
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeCache;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RecipeCache.class)
public class RecipeCacheMixin {

    @Unique
    private RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting>[] previousResults;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initPreviousResults(int size, CallbackInfo ci) {
        previousResults = new RecipeEvaluationResult[size];
    }

    @Inject(method = "get",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeCache$Entry;matches(Lnet/minecraft/world/item/crafting/CraftingInput;)Z",
            shift = At.Shift.AFTER
        )
    )
    private void applyPreviousResultOnGet(ServerLevel level, CraftingInput craftingInput, CallbackInfoReturnable<Optional<RecipeHolder<CraftingRecipe>>> cir, @Local int i) {
        if (craftingInput instanceof CraftingCustomInputDataExt) {
            ((CraftingCustomInputDataExt) craftingInput).setResultInfo(previousResults[i]);
        }
    }

    @Inject(method = "moveEntryToFront", at = @At("TAIL"))
    private void shiftPreviousResults(int index, CallbackInfo ci) {
        if (index > 0) {
            var previousResult = previousResults[index];
            System.arraycopy(previousResults, 0, previousResults, 1, index);
            previousResults[0] = previousResult;
        }
    }

    @Inject(method = "insert", at = @At("TAIL"))
    private void insertResult(CraftingInput input, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {
        // Always need to insert something and shift the previous results to keep it aligned with the vanilla entry indices.
        System.arraycopy(previousResults, 0, previousResults, 1, previousResults.length - 1);
        if (input instanceof CraftingCustomInputDataExt) {
            previousResults[0] = ((CraftingCustomInputDataExt) input).getResultInfo();
        } else {
            previousResults[0] = null;
        }
    }

}
