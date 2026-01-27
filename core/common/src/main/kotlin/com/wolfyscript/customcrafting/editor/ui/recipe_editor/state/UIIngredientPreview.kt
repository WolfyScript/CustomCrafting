package com.wolfyscript.customcrafting.editor.ui.recipe_editor.state

import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeCraftingModel
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
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
