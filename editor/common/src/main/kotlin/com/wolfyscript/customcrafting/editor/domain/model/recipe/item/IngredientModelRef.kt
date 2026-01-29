package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeCraftingModel

interface IngredientModelRef {

    val indexInCollection: Int

    fun resolveFor(collection: RecipeCraftingModel.IngredientCollectionModel): IngredientModel?

    fun toShapeId(ingredientCount: Int = 9): Char = indexInCollection.digitToChar(ingredientCount)

}