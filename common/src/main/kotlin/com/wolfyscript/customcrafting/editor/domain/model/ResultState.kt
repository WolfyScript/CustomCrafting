package com.wolfyscript.customcrafting.editor.domain.model

import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeChoicesModel
import com.wolfyscript.customcrafting.editor.domain.recipes.result.ResultActionState
import com.wolfyscript.customcrafting.editor.domain.recipes.result.ResultModel
import com.wolfyscript.customcrafting.editor.domain.recipes.result.ResultModifierState
import com.wolfyscript.customcrafting.recipes.RecipeItemModifier
import com.wolfyscript.customcrafting.recipes.RecipeItemModifierImpl
import com.wolfyscript.customcrafting.recipes.RecipeResult
import com.wolfyscript.customcrafting.recipes.RecipeResultImpl

class ResultModelImpl(
    override val choices: RecipeChoicesModel = RecipeChoicesModelImpl(),
    override val actions: MutableList<ResultActionState<*>> = mutableListOf(),
    override val modifier: ResultModifierState = ResultModifierStateImpl(),
) : ResultModel {

    override fun complete(): Result<RecipeResult> {
        val recipeChoices = choices.complete().getOrElse {
            return Result.failure(IllegalStateException("Failed to complete result", it))
        }
        if (recipeChoices.all().isEmpty()) {
            return Result.failure(IllegalArgumentException("Result must have at least one stack or tag."))
        }

        // TODO

        return Result.success(
            RecipeResultImpl(
                choices = recipeChoices,
                modifier = RecipeItemModifierImpl(),
                actions = mutableListOf(),
                bulkActions = mutableListOf(),
                alwaysKeepPrevious = false
            )
        )
    }
}

class ResultModifierStateImpl : ResultModifierState {

    override val transformations: MutableList<RecipeItemModifier.Transformation> = mutableListOf()

}