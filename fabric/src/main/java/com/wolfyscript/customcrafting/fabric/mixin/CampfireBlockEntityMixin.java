package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.CookingCustomInputExt;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState;
import com.wolfyscript.scafall.ScafallProvider;
import com.wolfyscript.scafall.identifier.Key;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin extends BlockEntity {

    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    private CampfireBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(at = @At("HEAD"), method = "placeFood")
    private void enterEvalContextOnPlace(ServerLevel level, LivingEntity entity, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        var wrapper = ScafallProvider.Companion.get().getMinecraftWrapper();
        var dimensionType = Key.key(level.dimension().location().getNamespace(), level.dimension().location().getPath());
        var wrappedPosition = wrapper.wrapVec3(worldPosition.getCenter(), dimensionType);
        EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(null, wrappedPosition));
    }

    @Inject(at = @At("RETURN"), method = "placeFood")
    private void exitEvalContextOnPlace(ServerLevel level, LivingEntity entity, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        EvaluationContextState.INSTANCE.exit();
    }

    @Inject(at = @At("HEAD"), method = "cookTick")
    private static void enterEvalContextOnCookTick(ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity campfire, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> check, CallbackInfo ci) {
        var wrapper = ScafallProvider.Companion.get().getMinecraftWrapper();
        var dimensionType = Key.key(level.dimension().location().getNamespace(), level.dimension().location().getPath());
        var wrappedPosition = wrapper.wrapVec3(pos.getCenter(), dimensionType);
        EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(null, wrappedPosition));
    }

    @Inject(at = @At("RETURN"), method = "cookTick")
    private static void exitEvalContextOnCookTick(ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity campfire, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> check, CallbackInfo ci) {
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
//        ((CookingCustomInputExt) input).setCustomInput(com.wolfyscript.customcrafting.recipes.data.RecipeInput.CookingRecipeInput.Companion.of(source, null));
//    }

    @Redirect(method = "placeFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private Optional<RecipeHolder<CampfireCookingRecipe>> injectCustomDataIntoSingleRecipeInputRedirect(RecipeManager instance, RecipeType<CampfireCookingRecipe> recipeType, RecipeInput input, Level level, ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
        var wrapper = ScafallProvider.Companion.get().getMinecraftWrapper();
        var source = wrapper.wrapMcStack(itemStack);
        ((CookingCustomInputExt) input).setCustomInput(com.wolfyscript.customcrafting.recipes.data.RecipeInput.CookingRecipeInput.Companion.of(source, null));

        return instance.getRecipeFor(recipeType, (SingleRecipeInput) input, level);
    }


    @ModifyVariable(method = "cookTick", at = @At(value = "STORE"))
    private static SingleRecipeInput injectCustomDataIntoSingleRecipeInput(
        SingleRecipeInput singleRecipeInput,
        ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity campfire, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> check
    ) {
        var wrapper = ScafallProvider.Companion.get().getMinecraftWrapper();
        var source = wrapper.wrapMcStack(singleRecipeInput.item());
        ((CookingCustomInputExt) (Object) singleRecipeInput).setCustomInput(com.wolfyscript.customcrafting.recipes.data.RecipeInput.CookingRecipeInput.Companion.of(source, null));
        return singleRecipeInput;
    }

}
