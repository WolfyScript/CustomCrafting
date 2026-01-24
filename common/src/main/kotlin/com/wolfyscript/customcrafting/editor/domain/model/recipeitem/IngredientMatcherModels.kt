package com.wolfyscript.customcrafting.editor.domain.model.recipeitem

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientMatcherModel
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.IngredientMatcherExactImpl
import com.wolfyscript.customcrafting.recipes.IngredientMatcherItemImpl
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatcher
import com.wolfyscript.scafall.identifier.Key

class ExactIngredientMatcherUIFactory : EditorUIFactory<IngredientMatcherModel<IngredientMatcher.Exact>> {

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientMatcherModel<IngredientMatcher.Exact> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientMatcherModel<IngredientMatcher.Exact> {
        return IngredientMatcherExactModel()
    }

    @Composable
    override fun renderUI() {
    }

}

class ItemIngredientMatcherUIFactory : EditorUIFactory<IngredientMatcherModel<IngredientMatcher.Item>> {

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientMatcherModel<IngredientMatcher.Item> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientMatcherModel<IngredientMatcher.Item> {
        return IngredientMatcherItemModel()
    }

    @Composable
    override fun renderUI() {}

}

class IngredientMatcherExactModel : IngredientMatcherModel<IngredientMatcher.Exact> {

    override fun complete(): Result<IngredientMatcher.Exact> {
        return Result.success(IngredientMatcherExactImpl())
    }

}

class IngredientMatcherItemModel(
    val mustContain: MutableSet<Key> = mutableSetOf(),
    val mustNotContain: MutableSet<Key> = mutableSetOf()
) : IngredientMatcherModel<IngredientMatcher.Item> {

    override fun complete(): Result<IngredientMatcher.Item> {
        return Result.success(IngredientMatcherItemImpl(mustContain, mustNotContain))
    }

}