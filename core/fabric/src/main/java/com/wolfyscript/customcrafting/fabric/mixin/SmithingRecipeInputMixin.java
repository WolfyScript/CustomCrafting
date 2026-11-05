package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSmithingCustomExt;
import com.wolfyscript.customcrafting.core.recipe.CustomRecipeSmithing;
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SmithingRecipeInput.class)
public class SmithingRecipeInputMixin implements RecipeInputSmithingCustomExt {

    @Unique
    @Nullable
    private RecipeInput.SmithingRecipeInput customInput = null;
    @Unique
    @Nullable
    private RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeSmithing> resultInfo = null;

    @Override
    public @Nullable RecipeInput.SmithingRecipeInput customcrafting_getCustomInput() {
        return customInput;
    }

    @Override
    public void customcrafting_setCustomInput(@Nullable RecipeInput.SmithingRecipeInput customInput) {
        this.customInput = customInput;
    }

    @Override
    public @Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeSmithing> customcrafting_getResultInfo() {
        return resultInfo;
    }

    @Override
    public void customcrafting_setResultInfo(@Nullable RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeSmithing> resultInfo) {
        this.resultInfo = resultInfo;
    }
}
