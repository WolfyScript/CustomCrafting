package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatcher
import com.wolfyscript.scafall.identifier.Key

interface ItemIngredientMatcherModel : IngredientMatcherModel<IngredientMatcher.Item> {

    val mustContain: MutableSet<Key>

    val mustNotContain: MutableSet<Key>

}