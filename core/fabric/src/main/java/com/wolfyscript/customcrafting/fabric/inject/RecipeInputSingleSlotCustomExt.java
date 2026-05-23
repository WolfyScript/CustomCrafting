package com.wolfyscript.customcrafting.fabric.inject;

import com.wolfyscript.customcrafting.core.recipe.CustomRecipe;
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput;

public interface RecipeInputSingleSlotCustomExt extends RecipeInputCustomExt<RecipeInput.SingleSlotRecipeInput, RecipeEvaluationResult<RecipeEvaluationResult.Data, ? extends CustomRecipe<RecipeInput.SingleSlotRecipeInput,?>>> {

}
