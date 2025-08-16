package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.CraftingCustomInputDataExt;
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting;
import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixDataImpl;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import com.wolfyscript.scafall.ScafallProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CraftingInput.class)
public class CraftingInputMixin implements CraftingCustomInputDataExt {

    @Unique
    @Nullable
    private RecipeInput.CraftingRecipeInput customInput = null;
    @Unique
    @Nullable
    private RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting> resultInfo = null;

    @Override
    public @Nullable RecipeInput.CraftingRecipeInput getCustomInput() {
        return customInput;
    }

    @Override
    public void setCustomInput(@Nullable RecipeInput.CraftingRecipeInput customInput) {
        this.customInput = customInput;
    }

    @Override
    public @Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting> getResultInfo() {
        return resultInfo;
    }

    @Override
    public void setResultInfo(@Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting> resultInfo) {
        this.resultInfo = resultInfo;
    }

    @Inject(method = "ofPositioned(IILjava/util/List;)Lnet/minecraft/world/item/crafting/CraftingInput$Positioned;", at = @At(value = "RETURN"))
    private static void addCustomInput(int i, int j, List<ItemStack> list, CallbackInfoReturnable<CraftingInput.Positioned> cir) {
        var positioned = cir.getReturnValue();
        var input = positioned.input();
        var matrixData = CraftingMatrixDataImpl.Companion.of(
            positioned,
            list.stream()
                .map((stack) -> ScafallProvider.Companion.get().getMinecraftWrapper().wrapMcStack(stack))
                .toList()
        );
        ((CraftingCustomInputDataExt) input).setCustomInput(RecipeInput.CraftingRecipeInput.Companion.of(matrixData));
    }

}
