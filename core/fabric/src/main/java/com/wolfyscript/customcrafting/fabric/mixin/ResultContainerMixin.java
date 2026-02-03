package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.CCResultContainerExt;
import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult;
import net.minecraft.world.inventory.ResultContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ResultContainer.class)
public abstract class ResultContainerMixin implements CCResultContainerExt {

    @Unique
    private RecipeEvaluationResult<?, ?> resultInfo = null;

    @Override
    public @Nullable RecipeEvaluationResult<?, ?> getResultInfo() {
        return resultInfo;
    }

    @Override
    public void setResultInfo(@Nullable RecipeEvaluationResult<?, ?> resultInfo) {
        this.resultInfo = resultInfo;
    }

}
