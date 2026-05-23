package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult;
import org.jetbrains.annotations.Nullable;

/**
 * Allows adding result info to a ResultContainer.
 * Care needs to be taken when setting and getting the result info, as the result info type is not specified.
 */
public interface CCResultContainerExt {

    @Nullable RecipeEvaluationResult<?,?> getResultInfo();

    void setResultInfo(@Nullable RecipeEvaluationResult<?,?> resultInfo);

    default boolean hasResultInfo() {
        return getResultInfo() != null;
    }

}
