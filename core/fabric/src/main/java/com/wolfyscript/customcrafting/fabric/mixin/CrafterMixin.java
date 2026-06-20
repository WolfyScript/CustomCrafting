package com.wolfyscript.customcrafting.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputCraftingCustomExt;
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext;
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContextState;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.minecraft.PositionWrappersKt;
import kotlin.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
        var wrappedPosition = PositionWrappersKt.wrap(Vec3.atCenterOf(pos), Key.fromMc(level.dimension().identifier()));
        EvaluationContextState.INSTANCE.enter(EvaluationContext.of(null, wrappedPosition));
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
    private void shrinkMatrix(
        BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci,
        @Local(name = "blockEntity") CrafterBlockEntity blockEntity, @Local(name = "craftInput") CraftingInput craftInput
    ) {
        var context = EvaluationContextState.INSTANCE.getCurrent();
        if (context == null) return;
        if (!(craftInput instanceof RecipeInputCraftingCustomExt customInputExt)) return;
        var resultInfo = customInputExt.getResultInfo();
        if (resultInfo == null) return;
        var customInput = customInputExt.getCustomInput();
        if (customInput == null) return;
        var recipe = resultInfo.getRecipe().getValue();
        if (recipe == null) return; // Recipe was removed in the meantime

        recipe.shrink(customInput, resultInfo, context, 1, (slot, stack) -> {
            blockEntity.setItem(slot, stack.unwrap());
            return Unit.INSTANCE;
        });

        ci.cancel(); // Override vanilla shrink logic
    }

}
