package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.RecipesState;
import com.wolfyscript.customcrafting.fabric.inject.CraftingStatePlayerExt;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements CraftingStatePlayerExt {

    @Unique
    private final RecipesState recipesState = new RecipesState();

    public RecipesState getCraftingState() {
        return recipesState;
    }

}
