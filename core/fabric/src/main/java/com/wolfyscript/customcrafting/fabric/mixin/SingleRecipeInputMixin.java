package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSingleSlotCustomExt;
import com.wolfyscript.customcrafting.core.recipe.CustomRecipe;
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.core.recipe.data.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SingleRecipeInput.class)
public class SingleRecipeInputMixin implements RecipeInputSingleSlotCustomExt {

    @Unique
    @Nullable
    private RecipeInput.SingleSlotRecipeInput customInput;
    @Unique
    @Nullable
    private RecipeEvaluationResult<RecipeEvaluationResult.Data, ? extends CustomRecipe<RecipeInput.SingleSlotRecipeInput,?>> resultInfo;

    @Override
    public RecipeInput.SingleSlotRecipeInput getCustomInput() {
        return customInput;
    }

    @Override
    public void setCustomInput(RecipeInput.@Nullable SingleSlotRecipeInput customInput) {
        this.customInput = customInput;
    }

    @Override
    public @Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, ? extends CustomRecipe<RecipeInput.SingleSlotRecipeInput,?>> getResultInfo() {
        return resultInfo;
    }

    @Override
    public void setResultInfo(@Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, ? extends CustomRecipe<RecipeInput.SingleSlotRecipeInput,?>> resultInfo) {
        this.resultInfo = resultInfo;
    }
}
