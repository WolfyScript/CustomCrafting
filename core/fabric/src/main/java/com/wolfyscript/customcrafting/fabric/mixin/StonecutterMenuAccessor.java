package com.wolfyscript.customcrafting.fabric.mixin;

import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.StonecutterMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StonecutterMenu.class)
public interface StonecutterMenuAccessor {

    @Accessor
    ResultContainer getResultContainer();

}
