package com.wolfyscript.customcrafting.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputCraftingCustomExt;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.utils.MinecraftWrapperKt;
import kotlin.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrafterBlock.class)
public class CrafterMixin {

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
            shift = At.Shift.AFTER
        ),
        method = "dispenseFrom"
    )
    private void enterEvalContext(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
        var wrappedPosition = MinecraftWrapperKt.wrap(pos.getCenter(), Key.fromMc(level.dimension().location()));
        EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(null, wrappedPosition));
    }

    @Inject(at = @At("RETURN"), method = "dispenseFrom")
    private void exitEvalContext(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
        EvaluationContextState.INSTANCE.exit();
    }

    @Inject(method = "dispenseFrom",
        at = @At(
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraft/world/level/block/CrafterBlock;dispenseItem(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/CrafterBlockEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/crafting/RecipeHolder;)V",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void shrinkMatrix(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci, @Local CrafterBlockEntity blockEntity, @Local CraftingInput craftingInput) {
        var context = EvaluationContextState.INSTANCE.getCurrent();
        if (context == null) return;
        if (!(craftingInput instanceof RecipeInputCraftingCustomExt customInputExt)) return;
        var resultInfo = customInputExt.getResultInfo();
        if (resultInfo == null) return;
        var customInput = customInputExt.getCustomInput();
        if (customInput == null) return;
        var recipe = resultInfo.getRecipe().getValue();
        if (recipe == null) return; // Recipe was removed in the meantime

        recipe.shrink(customInput, resultInfo, context, 1, (slot, stack) -> {
            blockEntity.setItem(slot, MinecraftWrapperKt.unwrap(stack));
            return Unit.INSTANCE;
        });

        ci.cancel(); // Override vanilla shrink logic
    }

}
