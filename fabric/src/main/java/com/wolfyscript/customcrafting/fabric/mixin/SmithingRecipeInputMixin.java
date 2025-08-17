package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSmithingCustomExt;
import com.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SmithingRecipeInput.class)
public class SmithingRecipeInputMixin implements RecipeInputSmithingCustomExt {

    @Nullable
    private RecipeInput.SmithingRecipeInput customInput = null;
    @Nullable
    private RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeSmithing> resultInfo = null;

    @Override
    public @Nullable RecipeInput.SmithingRecipeInput getCustomInput() {
        return customInput;
    }

    @Override
    public void setCustomInput(@Nullable RecipeInput.SmithingRecipeInput customInput) {
        this.customInput = customInput;
    }

    @Override
    public @Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeSmithing> getResultInfo() {
        return resultInfo;
    }

    @Override
    public void setResultInfo(@Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeSmithing> resultInfo) {
        this.resultInfo = resultInfo;
    }
}
