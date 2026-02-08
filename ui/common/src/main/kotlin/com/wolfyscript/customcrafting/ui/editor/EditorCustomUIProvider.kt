package com.wolfyscript.customcrafting.ui.editor

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModel

interface EditorCustomUIProvider<M, C> {

    val modelType: Class<M>

    @Composable
    fun C.render(model: M)

}

interface RecipeConditionCustomUIProvider<M, C> : EditorCustomUIProvider<M, C>

interface IngredientMatcherCustomUIProvider<M : IngredientMatcherModel<*>> :
    EditorCustomUIProvider<M, IngredientMatcherContext<M>>

interface IngredientConsumerCustomUIProvider<M : IngredientConsumerModel<*>> :
    EditorCustomUIProvider<M, IngredientConsumerContext>

interface IngredientRemainderCustomUIProvider<M : IngredientRemainderModel<*>> :
    EditorCustomUIProvider<M, IngredientRemainderContext>

interface RecipeItemTransmuterCustomUIProvider<M, C> : EditorCustomUIProvider<M, C>

interface RecipeResultActionCustomUIProvider<M, C> : EditorCustomUIProvider<M, C>

data class IngredientMatcherContext<M: IngredientMatcherModel<*>>(
    val updateMatcher: (newMatcher: M) -> Unit
)

class IngredientConsumerContext

class IngredientRemainderContext