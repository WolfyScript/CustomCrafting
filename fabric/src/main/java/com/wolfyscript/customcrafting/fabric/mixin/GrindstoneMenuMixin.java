package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.CustomCraftingProvider;
import com.wolfyscript.customcrafting.fabric.inject.GrindstoneResultSlotsExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeResultStateKt;
import com.wolfyscript.customcrafting.recipes.CustomRecipeGrinding;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.RecipeReference;
import com.wolfyscript.customcrafting.recipes.RecipeTypes;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import com.wolfyscript.customcrafting.recipes.process.ProcessGrinding;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.utils.MinecraftWrapperKt;
import kotlin.random.Random;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin extends AbstractContainerMenu {

    @Shadow
    @Final
    Container repairSlots;

    @Shadow
    @Final
    private Container resultSlots;
    @Unique
    private Player player;

    protected GrindstoneMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(at = @At("TAIL"), method = "<init>(ILnet/minecraft/world/entity/player/Inventory;)V")
    private void setPlayer(int containerId, Inventory playerInventory, CallbackInfo ci) {
        player = playerInventory.player;
    }

    @Inject(at = @At("TAIL"), method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V")
    private void setPlayer(int containerId, Inventory playerInventory, ContainerLevelAccess containerLevelAccess, CallbackInfo ci) {
        player = playerInventory.player;
    }

    @Inject(at = @At("HEAD"), method = "createResult", cancellable = true)
    private void computeCustomRecipeResult(CallbackInfo ci) {
        var customcrafting = CustomCraftingProvider.Companion.get();

        var level = player.level();
        var context = new EvaluationContextImpl(MinecraftWrapperKt.wrap(player), MinecraftWrapperKt.wrap(player.position(), Key.fromMc(level.dimension().location())));

        var input = RecipeInput.GrindingRecipeInput.Companion.of(MinecraftWrapperKt.wrap(repairSlots.getItem(0)), MinecraftWrapperKt.wrap(repairSlots.getItem(1)));

        var data = customcrafting.getRecipeManager().evaluateRecipesOfType(RecipeTypes.INSTANCE.getGrinding().resolveOrThrow(), input, context);
        if (data == null || data.getRecipe().getValue() == null) {
            return;
        }
        ci.cancel();

        var result = data.getRecipe().getValue().getProcess().compute(
            data,
            input,
            context,
            getResultRandom(player, data.getRecipe())
        );
        ((GrindstoneResultSlotsExt) getSlot(2)).setResultInfo(data);
        resultSlots.setItem(0, MinecraftWrapperKt.unwrap(result));
        broadcastChanges();
    }

    @Unique
    private Random getResultRandom(Player player, RecipeReference<CustomRecipeGrinding> recipe) {
        if (player instanceof ServerPlayer serverPlayer) {
            var key = recipe.getKey();
            if (recipe.getValue() == null) return Random.Default;
            var process = recipe.getValue().getProcess();
            if (process instanceof ProcessGrinding.FixedResultProcessGrinding fixedResultProcess) {
                return RecipeResultStateKt.getRecipeResultCachedRandom(serverPlayer, key, fixedResultProcess.getResult().getAlwaysKeepPrevious());
            }
            return RecipeResultStateKt.getRecipeResultCachedRandom(serverPlayer, key, false);
        }
        return Random.Default;

    }

}
