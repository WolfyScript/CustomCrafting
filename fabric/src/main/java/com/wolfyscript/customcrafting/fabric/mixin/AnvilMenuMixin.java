package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.CustomCraftingProvider;
import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.RecipeTypes;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import com.wolfyscript.scafall.ScafallProvider;
import com.wolfyscript.scafall.identifier.Key;
import kotlin.random.Random;
import kotlin.random.RandomKt;
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

    @Shadow
    @javax.annotation.Nullable
    private String itemName;
    @Shadow
    @Final
    private DataSlot cost;
    @Unique
    @Nullable
    private RecipeEvaluationResult<RecipeEvaluationResult.RepairingRecipeData, CustomRecipeRepairing> resultInfo = null;
    @Unique
    private long seed = Random.Default.nextLong();

    private AnvilMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(menuType, containerId, inventory, access, slotDefinition);
    }

    @Unique
    private void resetSeed() {
        seed = Random.Default.nextLong();
    }

    @Inject(at = @At("HEAD"), method = "createResult", cancellable = true)
    private void customRecipeLogic(CallbackInfo ci) {
        resultInfo = null;
        var customcrafting = CustomCraftingProvider.Companion.get();
        var wrapper = ScafallProvider.Companion.get().getMinecraftWrapper();
        var level = player.level();
        var key = Key.Companion.key(level.dimension().location().getNamespace(), level.dimension().location().getPath());
        var context = new EvaluationContextImpl(wrapper.wrapMcPlayer(player), wrapper.wrapVec3(player.position(), key));
        var input = RecipeInput.RepairingRecipeInput.Companion.of(wrapper.wrapMcStack(getSlot(0).getItem()), wrapper.wrapMcStack(getSlot(1).getItem()), itemName);

        var data = customcrafting.getRecipeManager().evaluateRecipesOfType(RecipeTypes.INSTANCE.getRepairing().resolveOrThrow(), input, context);
        if (data == null || data.getRecipe().getValue() == null) {
            return;
        }
        ci.cancel(); // Return before vanilla logic

        resultInfo = data;
        var recipe = data.getRecipe().getValue();
        var result = recipe.getProcess().compute(data, input, context, RandomKt.Random(seed));

        getSlot(getResultSlot()).set(wrapper.unwrapToMcStack(result));
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

        var data = resultInfo.getData();

        // TODO: Craft remains!
        var base = data.bySlot(0);
        if (base != null) {
            getSlot(0).getItem().shrink(base.getMatchedItemStackRef().getAmount());
        }
        var addition = data.bySlot(1);
        if (addition != null) {
            var count = addition.getMatchedItemStackRef().getAmount();
            if (data.getItemRepairCost() != null) {
                count *= data.getItemRepairCost();
            }
            getSlot(1).getItem().shrink(count);
        }

        resultInfo = null;
        cost.set(0);
        resetSeed();

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
