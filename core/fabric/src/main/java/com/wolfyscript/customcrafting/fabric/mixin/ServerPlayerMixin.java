package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.RecipeResultCacheExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeResultStateCache;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements RecipeResultCacheExt {

    @Unique
    private final RecipeResultStateCache cache = new RecipeResultStateCache();

    public RecipeResultStateCache customcrafting$getRecipeResultStateCache() {
        return cache;
    }

}
