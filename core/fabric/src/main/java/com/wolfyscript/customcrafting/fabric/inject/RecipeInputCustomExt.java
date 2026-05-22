package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.core.recipe.data.RecipeInput;
import org.jetbrains.annotations.Nullable;

public interface RecipeInputCustomExt<I extends RecipeInput, R extends RecipeEvaluationResult<?,?>> {

    @Nullable I getCustomInput();

    void setCustomInput(@Nullable I customInput);

    @Nullable R getResultInfo();

    void setResultInfo(@Nullable R resultInfo);

}
