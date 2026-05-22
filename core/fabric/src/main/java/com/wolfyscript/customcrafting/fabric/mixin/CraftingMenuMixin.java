package com.wolfyscript.customcrafting.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.fabric.inject.CCResultContainerExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputCraftingCustomExt;
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext;
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContextState;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.minecraft.PlayerWrappersKt;
import com.wolfyscript.scafall.wrappers.minecraft.PositionWrappersKt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {

    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;"))
    private static void enterEvalState(AbstractContainerMenu menu, ServerLevel level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {
        var wrappedPosition = PositionWrappersKt.wrap(player.position(), Key.fromMc(level.dimension().identifier()));
        EvaluationContextState.INSTANCE.enter(EvaluationContext.of(PlayerWrappersKt.wrap(player), wrappedPosition));
    }

    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "TAIL"))
    private static void exitEvalState(AbstractContainerMenu menu, ServerLevel level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {
        EvaluationContextState.INSTANCE.exit();
    }

    @Inject(
        method = "slotChangedCraftingGrid",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ResultContainer;setRecipeUsed(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/crafting/RecipeHolder;)Z",
            shift = At.Shift.AFTER
        )
    )
    private static void setEvalResult(AbstractContainerMenu menu, ServerLevel level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci, @Local CraftingInput craftingInput) {
        if (!(craftingInput instanceof RecipeInputCraftingCustomExt inputExt)) return;
        if (!(resultSlots instanceof CCResultContainerExt resultSlotsExt)) return;
        resultSlotsExt.setResultInfo(inputExt.getResultInfo());
    }

}
