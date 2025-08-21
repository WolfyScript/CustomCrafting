package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe;
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSingleSlotCustomExt;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.data.SingleSlotRecipeInputImpl;
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState;
import com.wolfyscript.scafall.wrappers.utils.MinecraftWrapperKt;
import net.minecraft.world.Container;
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
            EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(null, null));
            var stack = container.getItem(0);
            var input = new SingleRecipeInput(stack);
            ((RecipeInputSingleSlotCustomExt) (Object) input).setCustomInput(
                new SingleSlotRecipeInputImpl(MinecraftWrapperKt.wrap(stack))
            );
            if (recipe.matches(input, level)) {
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
