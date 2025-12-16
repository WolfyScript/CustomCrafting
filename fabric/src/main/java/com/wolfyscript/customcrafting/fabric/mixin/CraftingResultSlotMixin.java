package com.wolfyscript.customcrafting.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.CustomCraftingProvider;
import com.wolfyscript.customcrafting.fabric.inject.CCResultContainerExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputCraftingCustomExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeResultStateKt;
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.MinecraftWrapperKt;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public abstract class CraftingResultSlotMixin extends Slot {

    @Shadow
    @Final
    private CraftingContainer craftSlots;

    private CraftingResultSlotMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(
        method = "onTake",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ResultSlot;getRemainingItems(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/core/NonNullList;"
        ),
        cancellable = true
    )
    private void shrinkCustomRecipeMatrix(Player player, ItemStack stack, CallbackInfo ci, @Local CraftingInput craftingInput) {
        if (!(this.container instanceof CCResultContainerExt resultContainer) || !resultContainer.hasResultInfo()) return;
        var resultInfo = resultContainer.getResultInfo(); assert resultInfo != null;
        var customInput = ((RecipeInputCraftingCustomExt) craftingInput).getCustomInput();
        if (customInput == null || !(resultInfo.getRecipe().getValue() instanceof CustomRecipeCrafting craftingRecipe)) return;
        ci.cancel(); // Return before vanilla logic

        var level = player.level();
        var wrappedPosition = MinecraftWrapperKt.wrap(player.position(), Key.fromMc(level.dimension().identifier()));
        var context = new EvaluationContextImpl(MinecraftWrapperKt.wrap(player), wrappedPosition);

        RecipeResultStateKt.resetRecipeResult((ServerPlayer) player, resultInfo.getRecipe().getKey());

        craftingRecipe.shrink(customInput, (RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting>) resultInfo, context, 1, (slot, stack1) -> {
            craftSlots.setItem(slot, MinecraftWrapperKt.unwrap(stack1));
            return Unit.INSTANCE;
        });

    }

}
