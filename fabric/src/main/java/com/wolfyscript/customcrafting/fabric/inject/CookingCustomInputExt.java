package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import org.jetbrains.annotations.Nullable;

public interface CookingCustomInputExt {

    @Nullable RecipeInput.CookingRecipeInput getCustomInput();

    void setCustomInput(@Nullable RecipeInput.CookingRecipeInput customInput);

    @Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCooking> getResultInfo();

    void setResultInfo(@Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCooking> resultInfo);

}
