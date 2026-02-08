package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.editor.ext.EditorModelFactory
import com.wolfyscript.customcrafting.core.recipes.CustomRecipe
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatchers
import com.wolfyscript.customcrafting.core.recipes.ingredient.of
import com.wolfyscript.scafall.identifier.Key

class ExactIngredientMatcherModelFactory : EditorModelFactory<ExactIngredientMatcherModel> {

    override val modelType: Class<ExactIngredientMatcherModel> = ExactIngredientMatcherModel::class.java

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): ExactIngredientMatcherModel {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): ExactIngredientMatcherModel {
        return IngredientMatcherExactModel()
    }

}

class ItemIngredientMatcherModelFactory : EditorModelFactory<ItemIngredientMatcherModel> {

    override val modelType: Class<ItemIngredientMatcherModel> = ItemIngredientMatcherModel::class.java

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): ItemIngredientMatcherModel {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): ItemIngredientMatcherModel {
        return IngredientMatcherItemModel()
    }

}

class IngredientMatcherExactModel : ExactIngredientMatcherModel {

    override val type: Class<IngredientMatcher.Exact> = IngredientMatcher.Exact::class.java
    override val typeKey: Key = IngredientMatchers.exact.key.key

    override fun complete(): Result<IngredientMatcher.Exact> {
        return Result.success(IngredientMatcher.Exact.of())
    }

}

class IngredientMatcherItemModel(
    override val mustContain: MutableSet<Key> = mutableSetOf(),
    override val mustNotContain: MutableSet<Key> = mutableSetOf()
) : ItemIngredientMatcherModel {

    override val type: Class<IngredientMatcher.Item> = IngredientMatcher.Item::class.java
    override val typeKey: Key = IngredientMatchers.item.key.key

    override fun complete(): Result<IngredientMatcher.Item> {
        return Result.success(IngredientMatcher.Item.of(mustContain, mustNotContain))
    }

}