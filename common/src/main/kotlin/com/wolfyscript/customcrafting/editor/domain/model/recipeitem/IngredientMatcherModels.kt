package com.wolfyscript.customcrafting.editor.domain.model.recipeitem

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientMatcherModel
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

class ExactIngredientMatcherUIFactory : EditorUIFactory<IngredientMatcherModel<IngredientMatcher.Exact>> {

    override val modelType: Class<IngredientMatcherModel<IngredientMatcher.Exact>> = IngredientMatcherModel::class.java as Class<IngredientMatcherModel<IngredientMatcher.Exact>>

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientMatcherModel<IngredientMatcher.Exact> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientMatcherModel<IngredientMatcher.Exact> {
        return IngredientMatcherExactModel()
    }

    @Composable
    override fun renderUI(model: IngredientMatcherModel<IngredientMatcher.Exact>) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(stack = ItemStack(Items.CYAN_CONCRETE).apply {
                set(DataComponents.ITEM_NAME, "This matcher requires no configuration!".deser().vanilla())
            }.snapshot())
        }
    }

}

class ItemIngredientMatcherUIFactory : EditorUIFactory<IngredientMatcherModel<IngredientMatcher.Item>> {

    override val modelType: Class<IngredientMatcherModel<IngredientMatcher.Item>> = IngredientMatcherModel::class.java as Class<IngredientMatcherModel<IngredientMatcher.Item>>

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientMatcherModel<IngredientMatcher.Item> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientMatcherModel<IngredientMatcher.Item> {
        return IngredientMatcherItemModel()
    }

    @Composable
    override fun renderUI(model: IngredientMatcherModel<IngredientMatcher.Item>) {}

}

class IngredientMatcherExactModel : IngredientMatcherModel<IngredientMatcher.Exact> {

    override val type: Class<IngredientMatcher.Exact> = IngredientMatcher.Exact::class.java
    override val typeKey: Key = IngredientMatchers.exact.key.key

    override fun complete(): Result<IngredientMatcher.Exact> {
        return Result.success(IngredientMatcherExactImpl())
    }

}

class IngredientMatcherItemModel(
    val mustContain: MutableSet<Key> = mutableSetOf(),
    val mustNotContain: MutableSet<Key> = mutableSetOf()
) : IngredientMatcherModel<IngredientMatcher.Item> {

    override val type: Class<IngredientMatcher.Item> = IngredientMatcher.Item::class.java
    override val typeKey: Key = IngredientMatchers.item.key.key

    override fun complete(): Result<IngredientMatcher.Item> {
        return Result.success(IngredientMatcherItemImpl(mustContain, mustNotContain))
    }

}