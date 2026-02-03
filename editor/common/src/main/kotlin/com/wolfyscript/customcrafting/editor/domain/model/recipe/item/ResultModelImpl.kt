package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier
import com.wolfyscript.customcrafting.core.recipes.RecipeResult

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
            RecipeResult.of(
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

        return Result.success(RecipeItemModifier.of(completedTransformations))
    }

}