package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.recipes.data.RecipeInput;
import org.jetbrains.annotations.Nullable;

public interface SmithingCustomInputExt {

    @Nullable RecipeInput.SmithingRecipeInput getCustomInput();

    void setCustomInput(@Nullable RecipeInput.SmithingRecipeInput customInput);

    @Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeSmithing> getResultInfo();

    void setResultInfo(@Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeSmithing> resultInfo);

}
