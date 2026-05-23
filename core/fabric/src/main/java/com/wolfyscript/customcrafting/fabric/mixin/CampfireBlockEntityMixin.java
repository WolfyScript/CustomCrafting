package com.wolfyscript.customcrafting.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.core.recipe.CustomRecipeCooking;
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext;
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContextState;
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSingleSlotCustomExt;
import com.wolfyscript.scafall.wrappers.minecraft.EntityWrappersKt;
import com.wolfyscript.scafall.wrappers.minecraft.ItemStackWrappersKt;
import com.wolfyscript.scafall.wrappers.minecraft.PositionWrappersKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin extends BlockEntity {

    @Unique
    private static final int INPUT_SLOT = 0;

    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    @Shadow
    public abstract NonNullList<ItemStack> getItems();

    private CampfireBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(at = @At("HEAD"), method = "placeFood")
    private void enterEvalContextOnPlace(ServerLevel serverLevel, LivingEntity sourceEntity, ItemStack placeItem, CallbackInfoReturnable<Boolean> cir) {
        EvaluationContextState.INSTANCE.enter(EvaluationContext.of(
            null, null,
            PositionWrappersKt.wrap(worldPosition),
            EntityWrappersKt.wrap(this)
        ));
    }

    @Inject(at = @At("RETURN"), method = "placeFood")
    private void exitEvalContextOnPlace(ServerLevel serverLevel, LivingEntity sourceEntity, ItemStack placeItem, CallbackInfoReturnable<Boolean> cir) {
        EvaluationContextState.INSTANCE.exit();
    }

    @Inject(at = @At("HEAD"), method = "cookTick")
    private static void enterEvalContextOnCookTick(ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity entity, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> recipeCache, CallbackInfo ci) {
        EvaluationContextState.INSTANCE.enter(EvaluationContext.of(
            null, null,
            PositionWrappersKt.wrap(pos),
            EntityWrappersKt.wrap(entity)
        ));
    }

    @Inject(at = @At("RETURN"), method = "cookTick")
    private static void exitEvalContextOnCookTick(ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity entity, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> recipeCache, CallbackInfo ci) {
        EvaluationContextState.INSTANCE.exit();
    }

//    @ModifyArgs(
//        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"),
//        method = "placeFood"
//    )
//    private void injectCustomDataIntoSingleRecipeInput(Args args, ServerLevel level, LivingEntity entity, ItemStack stack) {
//        RecipeInput input = args.get(1);
//        var wrapper = ScafallProvider.Companion.get().getMinecraftWrapper();
//        var source = wrapper.wrapMcStack(items.get(0));
//        ((CookingCustomInputExt) input).setCustomInput(com.wolfyscript.customcrafting.core.recipes.data.RecipeInput.CookingRecipeInput.Companion.of(source, null));
//    }

    @Redirect(method = "placeFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private Optional<RecipeHolder<CampfireCookingRecipe>> injectCustomDataIntoSingleRecipeInputRedirect(RecipeManager instance, RecipeType<CampfireCookingRecipe> recipeType, RecipeInput input, Level level, ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
        ((RecipeInputSingleSlotCustomExt) input).setCustomInput(
            com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput.SingleSlotRecipeInput.Companion.of(ItemStackWrappersKt.wrap(itemStack))
        );
        return instance.getRecipeFor(recipeType, (SingleRecipeInput) input, level);
    }


    @ModifyVariable(method = "cookTick", at = @At(value = "STORE"))
    private static SingleRecipeInput injectCustomDataIntoSingleRecipeInput(
        SingleRecipeInput input,
        ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity entity, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> check
    ) {
        var source = ItemStackWrappersKt.wrap(input.item());
        ((RecipeInputSingleSlotCustomExt) (Object) input).setCustomInput(com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput.SingleSlotRecipeInput.Companion.of(source));
        return input;
    }

    @Inject(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
    private static void customCookTick(ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity campfire, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> check, CallbackInfo ci, @Local SingleRecipeInput singleRecipeInput, @Local int index) {
        var context = EvaluationContext.of(null, null, PositionWrappersKt.wrap(pos), EntityWrappersKt.wrap(campfire));

        var resultInfo = ((RecipeInputSingleSlotCustomExt) (Object) singleRecipeInput).getResultInfo();
        if (resultInfo == null || resultInfo.getRecipe().getValue() == null) {
            return;
        }
        var customRecipe = resultInfo.getRecipe().getValue();
        if (customRecipe instanceof CustomRecipeCooking cookingRecipe) {
            cookingRecipe.getResult().runActions(context, 1);

            var input = resultInfo.getData().bySlot(INPUT_SLOT);
            if (input != null) {
                var stack = ItemStackWrappersKt.wrap(campfire.getItems().get(index));
                stack = cookingRecipe.getProcessing().getSource().shrink(stack, 1, input.getMatchedItemStackRef(), context, resultInfo);
                campfire.getItems().set(index, stack.unwrap());
            }
        }
    }

    @Redirect(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private static <E> E preventInputReset(NonNullList<E> instance, int index, E element, @Local(name = "input") SingleRecipeInput input) {
        var resultInfo = ((RecipeInputSingleSlotCustomExt) (Object) input).getResultInfo();
        if (resultInfo == null || resultInfo.getRecipe().getValue() == null) {
            return instance.set(index, element);
        }
        // nop when it is a custom recipe
        return element;
    }

}
