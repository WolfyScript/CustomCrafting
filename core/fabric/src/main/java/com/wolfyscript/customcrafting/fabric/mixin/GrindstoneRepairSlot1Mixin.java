package com.wolfyscript.customcrafting.fabric.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$3")
public class GrindstoneRepairSlot1Mixin {

    @Inject(method = "mayPlace", at = @At(value = "RETURN"), cancellable = true)
    private void allowRecipeItems(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
    
}
