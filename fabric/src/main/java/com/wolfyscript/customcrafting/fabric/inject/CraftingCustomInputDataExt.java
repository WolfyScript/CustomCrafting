package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import org.jetbrains.annotations.Nullable;

public interface CraftingCustomInputDataExt {

    @Nullable RecipeInput.CraftingRecipeInput getCustomInput();

    void setCustomInput(@Nullable RecipeInput.CraftingRecipeInput customInput);

    @Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting> getResultInfo();

    void setResultInfo(@Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting> resultInfo);

}
