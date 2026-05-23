package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.core.recipe.CustomRecipeGrinding;
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult;
import org.jetbrains.annotations.Nullable;

public interface GrindstoneResultSlotsExt {

    void setResultInfo(@Nullable RecipeEvaluationResult<RecipeEvaluationResult.GrindingRecipeData, CustomRecipeGrinding> resultInfo);

    @Nullable
    RecipeEvaluationResult<RecipeEvaluationResult.GrindingRecipeData, CustomRecipeGrinding> getResultInfo();

}
