package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.CCResultContainerExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeResultStateKt;
import com.wolfyscript.customcrafting.recipes.CustomRecipeStonecutting;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.MinecraftWrapperKt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.inventory.StonecutterMenu$2")
public abstract class StonecutterResultSlotMixin extends Slot {

    private StonecutterResultSlotMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Unique
    private StonecutterMenu stonecutterMenu;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void onInit(StonecutterMenu this$0, Container container, int slot, int x, int y, ContainerLevelAccess par6, CallbackInfo ci) {
        this.stonecutterMenu = this$0;
    }

    @Redirect(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;remove(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack onCustomTake(Slot instance, int amount, Player player, ItemStack stack) {
        var resultContainer = ((StonecutterMenuAccessor) stonecutterMenu).getResultContainer();
        var resultInfo = ((CCResultContainerExt) resultContainer).getResultInfo();
        if (resultInfo == null || resultInfo.getRecipe().getValue() == null) {
            return instance.remove(amount);
        }

        var playerLevel = player.level();
        var context = new EvaluationContextImpl(MinecraftWrapperKt.wrap(player), MinecraftWrapperKt.wrap(player.position(), Key.fromMc(playerLevel.dimension().identifier())));
        RecipeResultStateKt.resetRecipeResult((ServerPlayer) player, resultInfo.getRecipe().getKey());

        var recipe = resultInfo.getRecipe().getValue();
        if (recipe instanceof CustomRecipeStonecutting customRecipe) {
            customRecipe.getResult().runActions(context, 1);

            var input = resultInfo.getData().bySlot(0);
            if (input != null) {
                var shrunken = MinecraftWrapperKt.unwrap(customRecipe.getSource().shrink(
                    MinecraftWrapperKt.wrap(stonecutterMenu.getSlot(0).getItem()),
                    1,
                    input.getMatchedItemStackRef(),
                    context,
                    resultInfo
                ));
                instance.set(shrunken);
                return shrunken;
            }
        }
        return instance.remove(amount);
    }

}
