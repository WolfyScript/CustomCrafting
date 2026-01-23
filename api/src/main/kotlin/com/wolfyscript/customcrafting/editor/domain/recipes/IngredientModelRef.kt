package com.wolfyscript.customcrafting.editor.domain.recipes

interface IngredientModelRef {

    val indexInCollection: Int

    fun resolveFor(collection: RecipeCraftingModel.IngredientCollectionModel): IngredientModel?

    fun toShapeId(ingredientCount: Int = 9): Char = indexInCollection.digitToChar(ingredientCount)

}