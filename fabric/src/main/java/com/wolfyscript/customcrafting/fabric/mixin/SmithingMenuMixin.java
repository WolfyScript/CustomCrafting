package com.wolfyscript.customcrafting.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSmithingCustomExt;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState;
import com.wolfyscript.scafall.ScafallProvider;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.utils.MinecraftWrapperKt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private Level level;

    private SmithingMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(menuType, containerId, inventory, access, slotDefinition);
    }

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
        ),
        method = "createResult"
    )
    private void addCustomInput(CallbackInfo ci, @Local SmithingRecipeInput smithingRecipeInput) {
        RecipeInput.SmithingRecipeInput customInput = RecipeInput.SmithingRecipeInput.Companion.of(
            MinecraftWrapperKt.wrap(getItems().get(0)),
            MinecraftWrapperKt.wrap(getItems().get(1)),
            MinecraftWrapperKt.wrap(getItems().get(2))
        );
        ((RecipeInputSmithingCustomExt)(Object) smithingRecipeInput).setCustomInput(customInput);
    }

    @Inject(
        at = @At(value = "HEAD"),
        method = "createResult"
    )
    private void enterEvalContext(CallbackInfo ci) {
        var levelKey = Key.Companion.key(level.dimension().location().getNamespace(), level.dimension().location().getPath());
        EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(MinecraftWrapperKt.wrap(player), MinecraftWrapperKt.wrap(player.position(), levelKey)));
    }

    @Inject(
        method = "createResult",
        at = @At("TAIL")
    )
    private void exitEvalContext(CallbackInfo ci) {
        EvaluationContextState.INSTANCE.exit();
    }

}
