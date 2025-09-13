package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.CustomCraftingProvider;
import com.wolfyscript.customcrafting.fabric.inject.RecipeResultStateKt;
import com.wolfyscript.customcrafting.recipes.*;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import com.wolfyscript.customcrafting.recipes.process.ProcessRepairing;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.MinecraftWrapperKt;
import kotlin.random.Random;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Unique
    private static final int BASE_SLOT = 0;
    @Unique
    private static final int ADDITION_SLOT = 1;
    @Unique
    private static final int RESET_COST = 0;

    @Shadow
    @javax.annotation.Nullable
    private String itemName;
    @Shadow
    @Final
    private DataSlot cost;
    @Unique
    @Nullable
    private RecipeEvaluationResult<RecipeEvaluationResult.RepairingRecipeData, CustomRecipeRepairing> resultInfo = null;

    private AnvilMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(menuType, containerId, inventory, access, slotDefinition);
    }

    @Inject(at = @At("HEAD"), method = "createResult", cancellable = true)
    private void customRecipeLogic(CallbackInfo ci) {
        resultInfo = null;
        var customcrafting = CustomCraftingProvider.Companion.get();
        var level = player.level();
        var context = new EvaluationContextImpl(MinecraftWrapperKt.wrap(player), MinecraftWrapperKt.wrap(player.position(), Key.fromMc(level.dimension().location())));
        var input = RecipeInput.RepairingRecipeInput.Companion.of(MinecraftWrapperKt.wrap(getSlot(0).getItem()), MinecraftWrapperKt.wrap(getSlot(1).getItem()), itemName);

        var data = customcrafting.getRecipeManager().evaluateRecipesOfType(RecipeTypes.INSTANCE.getRepairing().resolveOrThrow(), input, context);
        if (data == null || data.getRecipe().getValue() == null) {
            return;
        }
        ci.cancel(); // Return before vanilla logic

        resultInfo = data;
        var recipe = data.getRecipe().getValue();
        var result = recipe.getProcess().compute(data, input, context, getResultRandom(player, resultInfo.getRecipe()));

        getSlot(getResultSlot()).set(MinecraftWrapperKt.unwrap(result));
    }

    @Unique
    private Random getResultRandom(Player player, RecipeReference<CustomRecipeRepairing> recipe) {
        if (player instanceof ServerPlayer serverPlayer) {
            var key = recipe.getKey();
            if (recipe.getValue() == null) return Random.Default;
            var process = recipe.getValue().getProcess();
            if (process instanceof ProcessRepairing.FixedResult fixedResultProcess) {
                return RecipeResultStateKt.getRecipeResultCachedRandom(serverPlayer, key, fixedResultProcess.getResult().getAlwaysKeepPrevious());
            }
            return RecipeResultStateKt.getRecipeResultCachedRandom(serverPlayer, key, false);
        }
        return Random.Default;

    }


    @Inject(at = @At("HEAD"), method = "onTake", cancellable = true)
    private void onTakeCustomRecipeOutput(Player player, ItemStack stack, CallbackInfo ci) {
        if (resultInfo == null) {
            return;
        }
        if (resultInfo.getRecipe().getValue() == null) {
            return; // The recipe was removed in the meantime
        }
        ci.cancel(); // Bypass vanilla logic, use custom logic

        if (!player.hasInfiniteMaterials()) {
            player.giveExperienceLevels(-cost.get());
        }

        var playerLevel = player.level();
        var context = new EvaluationContextImpl(MinecraftWrapperKt.wrap(player), MinecraftWrapperKt.wrap(player.position(), Key.fromMc(playerLevel.dimension().location())));
        var data = resultInfo.getData();

        var base = data.bySlot(BASE_SLOT);
        if (base != null) {
            base.getSelectedIngredient().shrink(
                MinecraftWrapperKt.wrap(getSlot(BASE_SLOT).getItem()),
                1,
                base.getMatchedItemStackRef(),
                context,
                resultInfo
            );
        }
        var addition = data.bySlot(ADDITION_SLOT);
        if (addition != null) {
            var count = 1;
            if (data.getItemRepairCost() != null) {
                count = data.getItemRepairCost();
            }
            addition.getSelectedIngredient().shrink(
                MinecraftWrapperKt.wrap(getSlot(ADDITION_SLOT).getItem()),
                count,
                addition.getMatchedItemStackRef(),
                context,
                resultInfo
            );
        }

        RecipeResultStateKt.resetRecipeResult((ServerPlayer) player, resultInfo.getRecipe().getKey());
        resultInfo = null;
        cost.set(RESET_COST);

        // Copying the rest like text filtering and anvil damage logic from vanilla
        if (player instanceof ServerPlayer serverPlayer
            && !StringUtil.isBlank(itemName)
            && !inputSlots.getItem(0).getHoverName().getString().equals(itemName)) {
            serverPlayer.getTextFilter().processStreamMessage(itemName);
        }

        access.execute((level, blockPos) -> {
            BlockState blockState = level.getBlockState(blockPos);
            if (!player.hasInfiniteMaterials() && blockState.is(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
                BlockState blockState2 = AnvilBlock.damage(blockState);
                if (blockState2 == null) {
                    level.removeBlock(blockPos, false);
                    level.levelEvent(1029, blockPos, 0);
                } else {
                    level.setBlock(blockPos, blockState2, 2);
                    level.levelEvent(1030, blockPos, 0);
                }
            } else {
                level.levelEvent(1030, blockPos, 0);
            }
        });

    }

}
