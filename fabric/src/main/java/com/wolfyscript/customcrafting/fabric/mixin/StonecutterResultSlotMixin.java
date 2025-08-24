package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.CCResultContainerExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeResultStateKt;
import com.wolfyscript.customcrafting.recipes.CustomRecipeStonecutting;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.utils.MinecraftWrapperKt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.inventory.StonecutterMenu$2")
public class StonecutterResultSlotMixin {

    @Unique
    private StonecutterMenu stonecutterMenu;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void onInit(StonecutterMenu this$0, Container container, int slot, int x, int y, ContainerLevelAccess par6, CallbackInfo ci) {
        this.stonecutterMenu = this$0;
    }

    @Inject(
        method = "onTake",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ResultContainer;awardUsedRecipes(Lnet/minecraft/world/entity/player/Player;Ljava/util/List;)V",
            shift = At.Shift.AFTER
        )
    )
    private void onTakeCustom(Player player, ItemStack stack, CallbackInfo ci) {
        var resultContainer = ((StonecutterMenuAccessor) stonecutterMenu).getResultContainer();
        var resultInfo = ((CCResultContainerExt) resultContainer).getResultInfo();
        if (resultInfo == null || resultInfo.getRecipe().getValue() == null) {
            return;
        }
        var level = player.level();
        var dimensionType = Key.key(level.dimension().location().getNamespace(), level.dimension().location().getPath());
        var context = new EvaluationContextImpl(MinecraftWrapperKt.wrap(player), MinecraftWrapperKt.wrap(player.position(), dimensionType));
        RecipeResultStateKt.resetRecipeResult((ServerPlayer) player, resultInfo.getRecipe().getKey());
        ((CustomRecipeStonecutting) resultInfo.getRecipe().getValue()).getResult().runActions(context, 1);
    }

}
