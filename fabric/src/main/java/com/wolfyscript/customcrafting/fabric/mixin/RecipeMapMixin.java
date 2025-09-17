package com.wolfyscript.customcrafting.fabric.mixin;

import com.google.common.collect.ImmutableMultimap;
import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.CustomCraftingProvider;
import com.wolfyscript.customcrafting.fabric.inject.ProxyRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeMap.class)
public class RecipeMapMixin {

    @Unique
    private static final int VANILLA_RECIPE_PRIORITY = 0;

    @Inject(
        method = "create",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableMap;builder()Lcom/google/common/collect/ImmutableMap$Builder;",
            shift = At.Shift.AFTER
        )
    )
    private static void sortRecipesByPriority(
        Iterable<RecipeHolder<?>> recipes,
        CallbackInfoReturnable<RecipeMap> cir,
        @Local ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> builder
    ) {
        if (!CustomCraftingProvider.Companion.registered()) {
            return;
        }
        CustomCraftingProvider.Companion.get().getLogger().debug("Sorting recipes by priority...");
        builder.orderValuesBy((that, other) -> {
            var thatRecipe = that.value();
            var otherRecipe = other.value();
            var thatPriority = VANILLA_RECIPE_PRIORITY;
            var otherPriority = VANILLA_RECIPE_PRIORITY;

            if (thatRecipe instanceof ProxyRecipe thatProxyRecipe) {
                var recipe = thatProxyRecipe.getCustomRecipe().getValue();
                if (recipe != null) {
                    thatPriority = recipe.getPriority();
                }
            }
            if (otherRecipe instanceof ProxyRecipe otherProxyRecipe) {
                var recipe = otherProxyRecipe.getCustomRecipe().getValue();
                if (recipe != null) {
                    otherPriority = recipe.getPriority();
                }
            }
            CustomCraftingProvider.Companion.get().getLogger().debug("Sort {} ({}) <> {} ({})", that.id().location(), thatPriority, other.id().location(), otherPriority);
            return -1 * Integer.compare(thatPriority, otherPriority);
        });
    }

}
