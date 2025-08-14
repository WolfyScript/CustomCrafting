package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.CustomCraftingProvider;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState;
import com.wolfyscript.scafall.ScafallProvider;
import com.wolfyscript.scafall.identifier.Key;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CrafterBlock;
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
        CustomCraftingProvider.Companion.get().getLogger().info("Entering eval context for Crafter at {} in {}", pos, this.getClass());
        var wrapper = ScafallProvider.Companion.get().getMinecraftWrapper();
        var dimensionType = Key.key(level.dimension().location().getNamespace(), level.dimension().location().getPath());
        var wrappedPosition = wrapper.wrapVec3(pos.getCenter(), dimensionType);
        EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(null, wrappedPosition));
    }

    @Inject(at = @At("RETURN"), method = "dispenseFrom")
    private void exitEvalContext(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
        CustomCraftingProvider.Companion.get().getLogger().info("Exit eval context for Crafter at {} in {}", pos, this.getClass());
        EvaluationContextState.INSTANCE.exit();
    }

}
