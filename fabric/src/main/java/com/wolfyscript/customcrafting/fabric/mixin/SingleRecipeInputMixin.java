package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.RecipeInputCookingCustomExt;
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SingleRecipeInput.class)
public class SingleRecipeInputMixin implements RecipeInputCookingCustomExt {

    @Unique
    @Nullable
    private RecipeInput.CookingRecipeInput customInput;
    @Unique
    @Nullable
    private RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCooking> resultInfo;

    @Override
    public RecipeInput.CookingRecipeInput getCustomInput() {
        return customInput;
    }

    @Override
    public void setCustomInput(RecipeInput.@Nullable CookingRecipeInput customInput) {
        this.customInput = customInput;
    }

    @Override
    public @Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCooking> getResultInfo() {
        return resultInfo;
    }

    @Override
    public void setResultInfo(@Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCooking> resultInfo) {
        this.resultInfo = resultInfo;
    }
}
