package com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherExactModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ItemIngredientMatcherModel
import com.wolfyscript.customcrafting.ui.editor.IngredientMatcherContext
import com.wolfyscript.customcrafting.ui.editor.IngredientMatcherCustomUIProvider
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxSize
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.elements.Box
import com.wolfyscript.viewportl.gui.elements.Column
import com.wolfyscript.viewportl.gui.elements.Icon
import com.wolfyscript.viewportl.gui.elements.Row
import com.wolfyscript.viewportl.gui.model.Store
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class ExactIngredientMatcherUIProvider : IngredientMatcherCustomUIProvider<IngredientMatcherExactModel> {

    override val modelType: Class<IngredientMatcherExactModel> = IngredientMatcherExactModel::class.java

    @Composable
    override fun IngredientMatcherContext<IngredientMatcherExactModel>.render(model: IngredientMatcherExactModel) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Icon(stack = ItemStack(Items.CYAN_CONCRETE).apply {
                set(DataComponents.ITEM_NAME, "This matcher requires no configuration!".deser().vanilla())
            }.snapshot())
        }
    }

}

class ItemIngredientMatcherUIProvider : IngredientMatcherCustomUIProvider<ItemIngredientMatcherModel> {

    override val modelType: Class<ItemIngredientMatcherModel> = ItemIngredientMatcherModel::class.java

    @Composable
    override fun IngredientMatcherContext<ItemIngredientMatcherModel>.render(model: ItemIngredientMatcherModel) {
        Column(Modifier.fillMaxWidth()) {
            Row {



            }

        }
    }

}

private class ItemIngredientMatcherStore : Store()

@Composable
fun ItemIngredientMatcherMenu(
    matcher: ItemIngredientMatcherModel
) {
    // TODO:
    // - Add keys to must contain
    // - add keys to must not contain





}