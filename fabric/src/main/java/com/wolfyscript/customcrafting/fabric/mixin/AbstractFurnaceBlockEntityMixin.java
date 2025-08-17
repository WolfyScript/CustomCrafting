package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.RecipeInputCookingCustomExt;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import com.wolfyscript.customcrafting.recipes.state.EvaluationContextState;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.utils.MinecraftWrapperKt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "serverTick")
    private static void enterEvalContext(ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity furnace, CallbackInfo ci) {
        var dimensionType = Key.key(level.dimension().location().getNamespace(), level.dimension().location().getPath());
        var wrappedPosition = MinecraftWrapperKt.wrap(pos.getCenter(), dimensionType);
        EvaluationContextState.INSTANCE.enter(new EvaluationContextImpl(null, wrappedPosition));
    }

    @Inject(at = @At(value = "TAIL"), method = "serverTick")
    private static void exitEvalContext(ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity furnace, CallbackInfo ci) {
        EvaluationContextState.INSTANCE.exit();
    }

    @ModifyVariable(method = "serverTick", at = @At(value = "STORE"), ordinal = 0)
    private static SingleRecipeInput injectCustomDataIntoSingleRecipeInput(SingleRecipeInput singleRecipeInput, ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity furnace) {
        var source = MinecraftWrapperKt.wrap(furnace.getItem(0));
        var fuel = MinecraftWrapperKt.wrap(furnace.getItem(1));
        ((RecipeInputCookingCustomExt)(Object) singleRecipeInput).setCustomInput(RecipeInput.CookingRecipeInput.Companion.of(source, fuel));
        return singleRecipeInput;
    }

}
