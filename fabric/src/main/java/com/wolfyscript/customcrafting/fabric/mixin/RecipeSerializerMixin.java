package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomSmithingRecipeProxy;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeSerializer.class)
interface RecipeSerializerMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void registerProxyRecipes(CallbackInfo ci) {
        Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ResourceLocation.fromNamespaceAndPath("customcrafting", "smithing"),
            new CustomSmithingRecipeProxy.Serializer()
        );
    }

}
