package com.wolfyscript.customcrafting.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeCooking;
import com.wolfyscript.customcrafting.core.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput;
import com.wolfyscript.customcrafting.core.recipes.state.EvaluationContextState;
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSingleSlotCustomExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeResultCacheExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeResultStateCache;
import com.wolfyscript.scafall.wrappers.minecraft.EntityWrappersKt;
import com.wolfyscript.scafall.wrappers.minecraft.ItemStackWrappersKt;
import com.wolfyscript.scafall.wrappers.minecraft.PositionWrappersKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements RecipeResultCacheExt {

    @Unique
    private final RecipeResultStateCache resultStateCache = new RecipeResultStateCache();

    @Override
    public RecipeResultStateCache customcrafting$getRecipeResultStateCache() {
        return resultStateCache;
    }

    @Inject(at = @At(value = "HEAD"), method = "serverTick")
    private static void enterEvalContext(ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity, CallbackInfo ci) {
        EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(null, null, PositionWrappersKt.wrap(pos), EntityWrappersKt.wrap(entity)));
    }

    @Inject(at = @At(value = "TAIL"), method = "serverTick")
    private static void exitEvalContext(ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity, CallbackInfo ci) {
        EvaluationContextState.INSTANCE.exit();
    }

    @ModifyVariable(method = "serverTick", at = @At(value = "STORE"), name = "input")
    private static SingleRecipeInput injectCustomDataIntoSingleRecipeInput(SingleRecipeInput input, ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity) {
        var source = ItemStackWrappersKt.wrap(entity.getItem(0));
        ((RecipeInputSingleSlotCustomExt) (Object) input).setCustomInput(RecipeInput.SingleSlotRecipeInput.Companion.of(source));
        return input;
    }

    @WrapOperation(
        method = "serverTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private static void wrapShrinkItemAndProduceResult(
        NonNullList<ItemStack> items, ItemStack inputItemStack, ItemStack result, Operation<Void> original,
        ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity,
        @Local(name = "input") SingleRecipeInput input
    ) {
        var resultInfo = ((RecipeInputSingleSlotCustomExt) (Object) input).getResultInfo();
        if (resultInfo == null || !(resultInfo.getRecipe().getValue() instanceof CustomRecipeCooking cookingRecipe)) {
            // Not a custom recipe, use vanilla logic
            original.call(items, inputItemStack, result);
            return;
        }

        var context = new EvaluationContextImpl(null, null, PositionWrappersKt.wrap(pos), EntityWrappersKt.wrap(entity));
        var random = ((RecipeResultCacheExt) entity).customcrafting$getRecipeResultStateCache()
            .get(resultInfo.getRecipe().getKey(), cookingRecipe.getResult().getAlwaysKeepPrevious()).getRandom();
        var resultStack = cookingRecipe.getResult().compute(resultInfo, context, random).unwrap();
        var existingResultStack = items.get(2);

        if (existingResultStack.isEmpty()) {
            items.set(2, resultStack);
        } else if (ItemStack.isSameItemSameComponents(existingResultStack, resultStack)) {
            // TODO: Do we still need these checks? All of this should be checked by the canBurn method already
            if (existingResultStack.getCount() + resultStack.getCount() > existingResultStack.getMaxStackSize()) {
                // Custom recipe but failed to produce result
                return;
            }
            existingResultStack.grow(resultStack.getCount());
        }

        ((RecipeResultCacheExt) entity).customcrafting$getRecipeResultStateCache().reset(resultInfo.getRecipe().getKey());
        cookingRecipe.getResult().runActions(context, 1);

        var sourceSlotData = resultInfo.getData().bySlot(0);
        if (sourceSlotData != null) {
            var newStack = cookingRecipe.getProcessing().getSource().shrink(
                ItemStackWrappersKt.wrap(items.get(0)),
                1,
                sourceSlotData.getMatchedItemStackRef(),
                context,
                resultInfo
            );
            items.set(0, newStack.unwrap());
        }
    }

}
