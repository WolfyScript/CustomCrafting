package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientModelRef
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeCraftingModel

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