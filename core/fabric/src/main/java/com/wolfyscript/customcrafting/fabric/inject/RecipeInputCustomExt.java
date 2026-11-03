package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput;
import org.jetbrains.annotations.Nullable;

public interface RecipeInputCustomExt<I extends RecipeInput, R extends RecipeEvaluationResult<?,?>> {

    default @Nullable I getCustomInput() {
        throw new AssertionError("Implemented in Mixin");
    }

    default void setCustomInput(@Nullable I customInput) {
        throw new AssertionError("Implemented in Mixin");
    }

    default @Nullable R getResultInfo() {
        throw new AssertionError("Implemented in Mixin");
    }

    default void setResultInfo(@Nullable R resultInfo) {
        throw new AssertionError("Implemented in Mixin");
    }

}
