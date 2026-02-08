package com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModelCustom
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModelDefault
import com.wolfyscript.customcrafting.ui.editor.IngredientRemainderContext
import com.wolfyscript.customcrafting.ui.editor.IngredientRemainderCustomUIProvider

class IngredientRemainderUIDefault() : IngredientRemainderCustomUIProvider<IngredientRemainderModelDefault> {
    override val modelType: Class<IngredientRemainderModelDefault> = IngredientRemainderModelDefault::class.java

    @Composable
    override fun IngredientRemainderContext.render(model: IngredientRemainderModelDefault) {

    }

}

class IngredientRemainderUICustom() : IngredientRemainderCustomUIProvider<IngredientRemainderModelCustom> {
    override val modelType: Class<IngredientRemainderModelCustom> = IngredientRemainderModelCustom::class.java

    @Composable
    override fun IngredientRemainderContext.render(model: IngredientRemainderModelCustom) {

    }

}