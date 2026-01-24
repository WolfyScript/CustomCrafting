package com.wolfyscript.customcrafting.editor.domain.model.recipeitem

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientRemainderModel
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainder
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.wrap
import net.minecraft.world.item.ItemStack

class IngredientRemainderModelDefault(
    val ignoreOptions: RemainsIgnoreOptions = RemainsIgnoreOptionsImpl(vanilla = false, others = false),
) : IngredientRemainderModel<IngredientRemainder.Default> {

    override fun complete(): Result<IngredientRemainder.Default> {
        return Result.success(IngredientRemainderDefaultImpl(ignoreOptions))
    }

}

class IngredientRemainderModelCustom(
    val ignoreOptions: RemainsIgnoreOptions = RemainsIgnoreOptionsImpl(vanilla = false, others = false),
    val remainder: ItemStackRef = ItemStackRef.create(ItemStack.EMPTY.wrap()),
) : IngredientRemainderModel<IngredientRemainder.Custom> {

    override fun complete(): Result<IngredientRemainder.Custom> {
        return Result.success(IngredientRemainderCustomImpl(ignoreOptions, remainder))
    }

}

class IngredientRemainderModelDefaultUIFactory :
    EditorUIFactory<IngredientRemainderModel<IngredientRemainder.Default>> {

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientRemainderModel<IngredientRemainder.Default> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientRemainderModel<IngredientRemainder.Default> {
        return IngredientRemainderModelDefault()
    }

    @Composable
    override fun renderUI() {
    }

}

class IngredientRemainderModelCustomUIFactory : EditorUIFactory<IngredientRemainderModel<IngredientRemainder.Custom>> {

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientRemainderModel<IngredientRemainder.Custom> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientRemainderModel<IngredientRemainder.Custom> {
        return IngredientRemainderModelCustom()
    }

    @Composable
    override fun renderUI() {
    }

}