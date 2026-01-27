package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.CustomCraftingProvider;
import com.wolfyscript.customcrafting.fabric.inject.RecipeManagerCustomRecipesExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeRegistrationUtils;
import com.wolfyscript.scafall.ScafallProvider;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerCustomRecipeMixin implements RecipeManagerCustomRecipesExt {

    @Shadow
    private RecipeMap recipes;

    @Shadow
    public abstract void finalizeRecipeLoading(FeatureFlagSet enabledFeatures);

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;", at = @At("RETURN"), cancellable = true)
    private void registerCustomRecipeProxies(ResourceManager resourceManager, ProfilerFiller profilerFiller, @NotNull CallbackInfoReturnable<RecipeMap> cir) {
        var current = cir.getReturnValue();
        LOGGER.info("Registering Vanilla Recipes");

        // Reload the custom recipes when the vanilla resources are reloaded.
        // Not called on initial startup, since CustomCrafting is not loaded yet.
        if (CustomCraftingProvider.Companion.registered()) {
            CustomCraftingProvider.Companion.get().getLogger().info("Registering Proxy Recipes");
            List<RecipeHolder<?>> newList = new ArrayList<>(current.values());
            newList.addAll(RecipeRegistrationUtils.INSTANCE.registerProxyRecipes());

            cir.setReturnValue(RecipeMap.create(newList));
        }
    }

    @Override
    public void registerProxyRecipes() {
        CustomCraftingProvider.Companion.get().getLogger().info("Registering Proxy Recipes");
        var filteredRecipes = RecipeRegistrationUtils.INSTANCE.removeExistingProxyRecipes(this.recipes.values());
        filteredRecipes.addAll(RecipeRegistrationUtils.INSTANCE.registerProxyRecipes());

        this.recipes = RecipeMap.create(filteredRecipes);
        finalizeRecipeLoading(ScafallProvider.Companion.get().getServer().getMinecraftServer().getWorldData().enabledFeatures());
    }
}
