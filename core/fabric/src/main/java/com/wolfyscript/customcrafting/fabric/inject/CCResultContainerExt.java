package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult;
import org.jetbrains.annotations.Nullable;

/**
 * Allows adding result info to a ResultContainer.
 * Care needs to be taken when setting and getting the result info, as the result info type is not specified.
 */
public interface CCResultContainerExt {

    default @Nullable RecipeEvaluationResult<?,?> customCrafting_getResultInfo() {
        throw new AssertionError("Implemented in Mixin");
    }

    default void customcrafting_setResultInfo(@Nullable RecipeEvaluationResult<?,?> resultInfo) {
        throw new AssertionError("Implemented in Mixin");
    }

    default boolean hasResultInfo() {
        return customCrafting_getResultInfo() != null;
    }

}
