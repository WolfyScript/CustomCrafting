package com.wolfyscript.customcrafting.ui.editor.recipe_editor.state

import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeCraftingModel
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.minecraft.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import net.minecraft.world.item.ItemStack

interface UIIngredientPreview {

    val icon: ItemStackSnapshot

    data class Custom(
        override val icon: ItemStackSnapshot,
        val replaceWithRemains: Boolean,
    ) : UIIngredientPreview

    data class Saved(
        val key: Key, override val icon: ItemStackSnapshot,
    ) : UIIngredientPreview

}

fun IngredientModel.toPreview(): UIIngredientPreview? {
    return when (this) {
        is IngredientModel.CustomIngredientModel -> UIIngredientPreview.Custom(
            choices.stacks.firstOrNull()?.create()?.snapshot() ?: ItemStack.EMPTY.snapshot(),
            replaceWithRemains
        )

        is IngredientModel.SavedIngredientModel -> UIIngredientPreview.Saved(
            key,
            icon,
        )

        else -> null
    }
}

fun RecipeCraftingModel.IngredientCollectionModel.toUIState(): List<UIIngredientPreview> {
    return ingredients.mapNotNull { it.toPreview() }
}
