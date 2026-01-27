package com.wolfyscript.customcrafting.editor.domain.model

import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModelRef
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeCraftingModel

data class IngredientModelRefImpl(override val indexInCollection: Int) : IngredientModelRef {

    override fun resolveFor(collection: RecipeCraftingModel.IngredientCollectionModel): IngredientModel? {
        return collection.ingredients.getOrNull(indexInCollection)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IngredientModelRefImpl) return false

        if (indexInCollection != other.indexInCollection) return false

        return true
    }

    override fun hashCode(): Int {
        return indexInCollection
    }


}