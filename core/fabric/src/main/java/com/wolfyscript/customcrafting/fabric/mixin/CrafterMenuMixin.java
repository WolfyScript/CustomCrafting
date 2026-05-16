package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.core.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.core.recipes.state.EvaluationContextState;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.minecraft.PlayerWrappersKt;
import com.wolfyscript.scafall.wrappers.minecraft.PositionWrappersKt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrafterMenu.class)
public class CrafterMenuMixin {

    @Shadow
    @Final
    private Player player;

    @Shadow
    @Final
    private CraftingContainer container;

    @Inject(at = @At("HEAD"), method = "refreshRecipeResult")
    private void enterEvalContext(CallbackInfo ci) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            var level = serverPlayer.level();
            var wrappedPosition = PositionWrappersKt.wrap(serverPlayer.position(), Key.fromMc(level.dimension().identifier()));
            EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(PlayerWrappersKt.wrap(serverPlayer), wrappedPosition));
        }
    }

    @Inject(at = @At("RETURN"), method = "refreshRecipeResult")
    private void exitEvalContext(CallbackInfo ci) {
        EvaluationContextState.INSTANCE.exit();
    }

}
