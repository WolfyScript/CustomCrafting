package com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerConsumeModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerKeepModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerReplaceModel
import com.wolfyscript.customcrafting.ui.editor.IngredientConsumerContext
import com.wolfyscript.customcrafting.ui.editor.IngredientConsumerCustomUIProvider

class IngredientConsumerConsumeUIProvider : IngredientConsumerCustomUIProvider<IngredientConsumerConsumeModel> {
    override val modelType: Class<IngredientConsumerConsumeModel> = IngredientConsumerConsumeModel::class.java

    @Composable
    override fun IngredientConsumerContext.render(model: IngredientConsumerConsumeModel) {

    }

}

class IngredientConsumerReplaceUIProvider : IngredientConsumerCustomUIProvider<IngredientConsumerReplaceModel> {

    override val modelType: Class<IngredientConsumerReplaceModel> = IngredientConsumerReplaceModel::class.java

    @Composable
    override fun IngredientConsumerContext.render(model: IngredientConsumerReplaceModel) {

    }

}

class IngredientConsumerKeepUIProvider : IngredientConsumerCustomUIProvider<IngredientConsumerKeepModel> {

    override val modelType: Class<IngredientConsumerKeepModel> = IngredientConsumerKeepModel::class.java

    @Composable
    override fun IngredientConsumerContext.render(model: IngredientConsumerKeepModel) {

    }

}