package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.CCResultContainerExt;
import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe;
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSingleSlotCustomExt;
import com.wolfyscript.customcrafting.core.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.core.recipes.data.SingleSlotRecipeInputImpl;
import com.wolfyscript.customcrafting.core.recipes.state.EvaluationContextState;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.MinecraftWrapperKt;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StonecutterMenu.class)
public class StonecutterMenuMixin {

    @Shadow
    @Final
    private Level level;

    @Shadow
    @Final
    ResultContainer resultContainer;

    @Shadow
    @Final
    Slot resultSlot;

    @Shadow
    @Final
    public Container container;

    @Unique
    private Player player;

    @Inject(at = @At("TAIL"), method = "<init>(ILnet/minecraft/world/entity/player/Inventory;)V")
    private void setPlayer(int containerId, Inventory playerInventory, CallbackInfo ci) {
        player = playerInventory.player;
    }

    @Inject(at = @At("TAIL"), method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V")
    private void setPlayer(int containerId, Inventory playerInventory, ContainerLevelAccess containerLevelAccess, CallbackInfo ci) {
        player = playerInventory.player;
    }

    /**
     * We can't really modify the recipe selection entries without messing up the indices between client and server
     * (client mod would be required to do this).
     * Instead, just check if the recipe is custom and matches before assembling the result.
     */
    @Inject(method = "method_64655", at = @At("HEAD"), cancellable = true)
    private void matchCustomProxyRecipe(RecipeHolder<StonecutterRecipe> recipeHolder, CallbackInfo ci) {
        var recipe = recipeHolder.value();
        if (recipe instanceof ProxyRecipe) {
            ci.cancel();
            EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(MinecraftWrapperKt.wrap(player), MinecraftWrapperKt.wrap(player.position(), Key.fromMc(level.dimension().identifier()))));
            var stack = container.getItem(0);
            var input = new SingleRecipeInput(stack);
            ((RecipeInputSingleSlotCustomExt) (Object) input).setCustomInput(
                new SingleSlotRecipeInputImpl(MinecraftWrapperKt.wrap(stack))
            );
            if (recipe.matches(input, level)) {
                ((CCResultContainerExt) resultContainer).setResultInfo(
                    ((RecipeInputSingleSlotCustomExt) (Object) input).getResultInfo()
                );
                resultContainer.setRecipeUsed(recipeHolder);
                resultSlot.set(recipe.assemble(input, level.registryAccess()));
            } else {
                resultContainer.setRecipeUsed(null);
                resultSlot.set(ItemStack.EMPTY);
            }
            EvaluationContextState.INSTANCE.exit();
        }
    }

}
