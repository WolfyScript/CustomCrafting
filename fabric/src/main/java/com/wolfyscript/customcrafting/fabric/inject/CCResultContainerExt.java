package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import org.jetbrains.annotations.Nullable;

public interface CCResultContainerExt {

    @Nullable RecipeEvaluationResult<?,?> getResultInfo();

    void setResultInfo(@Nullable RecipeEvaluationResult<?,?> resultInfo);

    default boolean hasResultInfo() {
        return getResultInfo() != null;
    }

}
