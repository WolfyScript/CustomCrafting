package com.wolfyscript.customcrafting.editor.ui.recipe_editor.state

import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import net.minecraft.world.item.ItemStack

interface UIIngredientPreview {

    val origin: IngredientModel

    data class Custom(
        @Deprecated("temporary. to be replaced with use-case system")
        override val origin: IngredientModel.CustomIngredientModel,
        val icon: ItemStackSnapshot,
        val replaceWithRemains: Boolean,
    ) : UIIngredientPreview

    data class Saved(
        @Deprecated("temporary. to be replaced with use-case system")
        override val origin: IngredientModel.SavedIngredientModel,
        val key: Key,
    ) : UIIngredientPreview

}

fun IngredientModel.toPreview(): UIIngredientPreview? {
    return when (this) {
        is IngredientModel.CustomIngredientModel -> UIIngredientPreview.Custom(
            this,
            stacks.firstOrNull()?.create()?.snapshot() ?: ItemStack.EMPTY.snapshot(),
            replaceWithRemains
        )

        is IngredientModel.SavedIngredientModel -> UIIngredientPreview.Saved(
            this,
            key
        )

        else -> null
    }
}
