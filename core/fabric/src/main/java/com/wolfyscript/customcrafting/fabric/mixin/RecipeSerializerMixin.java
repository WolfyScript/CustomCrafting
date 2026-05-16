package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomSmithingRecipeProxy;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeSerializers.class)
class RecipeSerializerMixin {

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void registerProxyRecipes(Registry<RecipeSerializer<?>> registry, CallbackInfoReturnable<Object> cir) {
        Registry.register(
            registry,
            Identifier.fromNamespaceAndPath("customcrafting", "smithing"),
            CustomSmithingRecipeProxy.SERIALIZER
        );
    }

}
