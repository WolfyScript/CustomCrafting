package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSingleSlotCustomExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeResultCacheExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeResultStateCache;
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState;
import com.wolfyscript.scafall.wrappers.MinecraftWrapperKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements RecipeResultCacheExt {

    @Unique
    private final RecipeResultStateCache resultStateCache = new RecipeResultStateCache();

    @Override
    public RecipeResultStateCache customcrafting$getRecipeResultStateCache() {
        return resultStateCache;
    }

    @Shadow
    private static boolean burn(RegistryAccess par1, RecipeHolder<? extends AbstractCookingRecipe> par2, SingleRecipeInput par3, NonNullList<ItemStack> par4, int par5) {
        return false;
    }

    @Shadow
    protected abstract NonNullList<ItemStack> getItems();

    @Shadow
    protected static boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<? extends AbstractCookingRecipe> recipe, SingleRecipeInput recipeInput, NonNullList<ItemStack> items, int maxStackSize) {
        return false;
    }

    @Inject(at = @At(value = "HEAD"), method = "serverTick")
    private static void enterEvalContext(ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity furnace, CallbackInfo ci) {
        EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(null, null, MinecraftWrapperKt.wrap(pos), MinecraftWrapperKt.wrap(furnace)));
    }

    @Inject(at = @At(value = "TAIL"), method = "serverTick")
    private static void exitEvalContext(ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity furnace, CallbackInfo ci) {
        EvaluationContextState.INSTANCE.exit();
    }

    @ModifyVariable(method = "serverTick", at = @At(value = "STORE"), ordinal = 0)
    private static SingleRecipeInput injectCustomDataIntoSingleRecipeInput(SingleRecipeInput singleRecipeInput, ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity furnace) {
        var source = MinecraftWrapperKt.wrap(furnace.getItem(0));
        ((RecipeInputSingleSlotCustomExt) (Object) singleRecipeInput).setCustomInput(RecipeInput.SingleSlotRecipeInput.Companion.of(source));
        return singleRecipeInput;
    }

    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/core/NonNullList;I)Z"
        ),
        method = "serverTick"
    )
    private static boolean shrinkItemAndProduceResult(
        RegistryAccess registryAccess,
        RecipeHolder<? extends AbstractCookingRecipe> recipe,
        SingleRecipeInput singleRecipeInput,
        NonNullList<ItemStack> items,
        int maxStackSize,
        ServerLevel level, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity entity
    ) {
        var resultInfo = ((RecipeInputSingleSlotCustomExt) (Object) singleRecipeInput).getResultInfo();
        if (resultInfo == null || resultInfo.getRecipe().getValue() == null) {
            // Not a custom recipe, use vanilla logic
            if (recipe != null && canBurn(registryAccess, recipe, singleRecipeInput, items, maxStackSize)) {
                return burn(registryAccess, recipe, singleRecipeInput, items, maxStackSize);
            }
            return false;
        }

        var context = new EvaluationContextImpl(null, null, MinecraftWrapperKt.wrap(blockPos), MinecraftWrapperKt.wrap(entity));

        var customRecipe = resultInfo.getRecipe().getValue();
        if (!(customRecipe instanceof CustomRecipeCooking cookingRecipe)) {
            return false;
        }
        var random = ((RecipeResultCacheExt) entity).customcrafting$getRecipeResultStateCache()
            .get(resultInfo.getRecipe().getKey(), cookingRecipe.getResult().getAlwaysKeepPrevious()).getRandom();
        var resultStack = MinecraftWrapperKt.unwrap(cookingRecipe.getResult().compute(resultInfo, context, random));
        if (resultStack.isEmpty()) {
            return false;
        }

        var existingResultStack = items.get(2);
        if (existingResultStack.isEmpty()) {
            items.set(2, resultStack);
        } else if (ItemStack.isSameItemSameComponents(existingResultStack, resultStack)) {
            if (existingResultStack.getCount() + resultStack.getCount() > existingResultStack.getMaxStackSize()) {
                // Custom recipe but failed to produce result
                return false;
            }
            existingResultStack.grow(resultStack.getCount());
        }

        ((RecipeResultCacheExt) entity).customcrafting$getRecipeResultStateCache().reset(resultInfo.getRecipe().getKey());
        cookingRecipe.getResult().runActions(context, 1);

        var sourceSlotData = resultInfo.getData().bySlot(0);
        if (sourceSlotData != null) {
            var newStack = cookingRecipe.getProcessing().getSource().shrink(
                MinecraftWrapperKt.wrap(items.get(0)),
                1,
                sourceSlotData.getMatchedItemStackRef(),
                context,
                resultInfo
            );
            items.set(0, MinecraftWrapperKt.unwrap(newStack));
        }

        return true;
    }

}
