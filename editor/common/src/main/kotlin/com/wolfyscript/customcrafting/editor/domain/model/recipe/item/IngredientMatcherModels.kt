package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.IngredientMatcherExactImpl
import com.wolfyscript.customcrafting.recipes.IngredientMatcherItemImpl
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatchers
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxSize
import com.wolfyscript.viewportl.gui.elements.Box
import com.wolfyscript.viewportl.gui.elements.Icon
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class ExactIngredientMatcherUIFactory : EditorUIFactory<ExactIngredientMatcherModel> {

    override val modelType: Class<ExactIngredientMatcherModel> = ExactIngredientMatcherModel::class.java

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): ExactIngredientMatcherModel {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): ExactIngredientMatcherModel {
        return IngredientMatcherExactModel()
    }

    @Composable
    override fun renderUI(model: ExactIngredientMatcherModel) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Icon(stack = ItemStack(Items.CYAN_CONCRETE).apply {
                set(DataComponents.ITEM_NAME, "This matcher requires no configuration!".deser().vanilla())
            }.snapshot())
        }
    }

}

class ItemIngredientMatcherUIFactory : EditorUIFactory<ItemIngredientMatcherModel> {

    override val modelType: Class<ItemIngredientMatcherModel> = ItemIngredientMatcherModel::class.java

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): ItemIngredientMatcherModel {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): ItemIngredientMatcherModel {
        return IngredientMatcherItemModel()
    }

    @Composable
    override fun renderUI(model: ItemIngredientMatcherModel) {




    }

}

class IngredientMatcherExactModel : ExactIngredientMatcherModel {

    override val type: Class<IngredientMatcher.Exact> = IngredientMatcher.Exact::class.java
    override val typeKey: Key = IngredientMatchers.exact.key.key

    override fun complete(): Result<IngredientMatcher.Exact> {
        return Result.success(IngredientMatcherExactImpl())
    }

}

class IngredientMatcherItemModel(
    override val mustContain: MutableSet<Key> = mutableSetOf(),
    override val mustNotContain: MutableSet<Key> = mutableSetOf()
) : ItemIngredientMatcherModel {

    override val type: Class<IngredientMatcher.Item> = IngredientMatcher.Item::class.java
    override val typeKey: Key = IngredientMatchers.item.key.key

    override fun complete(): Result<IngredientMatcher.Item> {
        return Result.success(IngredientMatcherItemImpl(mustContain, mustNotContain))
    }

}