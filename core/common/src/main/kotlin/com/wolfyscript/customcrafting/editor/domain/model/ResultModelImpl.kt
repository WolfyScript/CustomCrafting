package com.wolfyscript.customcrafting.editor.domain.model

import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeChoicesModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.ResultActionModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.ResultModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.RecipeItemModifierModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.TransformationModel
import com.wolfyscript.customcrafting.recipes.RecipeItemModifier
import com.wolfyscript.customcrafting.recipes.RecipeItemModifierImpl
import com.wolfyscript.customcrafting.recipes.RecipeResult
import com.wolfyscript.customcrafting.recipes.RecipeResultImpl

class ResultModelImpl(
    override val choices: RecipeChoicesModel = RecipeChoicesModelImpl(),
    override val actions: MutableList<ResultActionModel<*>> = mutableListOf(),
    override val modifier: RecipeItemModifierModel = RecipeItemModifierModelImpl(),
) : ResultModel {

    override fun complete(): Result<RecipeResult> {
        val recipeChoices = choices.complete().getOrElse {
            return Result.failure(IllegalStateException("Failed to complete result", it))
        }
        if (recipeChoices.all().isEmpty()) {
            return Result.failure(IllegalArgumentException("Result must have at least one stack or tag."))
        }

        val itemModifier = modifier.complete().getOrElse {
            return Result.failure(IllegalStateException("Failed to complete result", it))
        }

        // TODO

        return Result.success(
            RecipeResultImpl(
                choices = recipeChoices,
                modifier = itemModifier,
                actions = mutableListOf(),
                bulkActions = mutableListOf(),
                alwaysKeepPrevious = false
            )
        )
    }
}

class RecipeItemModifierModelImpl(
    override val transformations: MutableList<TransformationModel> = mutableListOf()
) : RecipeItemModifierModel {

    override fun complete(): Result<RecipeItemModifier> {
        val completedTransformations = transformations.mapIndexed { index, transformationModel ->
            transformationModel.complete().getOrElse {
                return Result.failure(IllegalStateException("Failed to complete recipe item modifier: invalid transformation at index $index", it))
            }
        }

        return Result.success(RecipeItemModifierImpl(completedTransformations))
    }

}