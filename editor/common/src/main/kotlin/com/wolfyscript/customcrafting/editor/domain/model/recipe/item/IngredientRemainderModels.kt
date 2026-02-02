package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.recipes.ingredient.of
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.wrap
import net.minecraft.world.item.ItemStack

class IngredientRemainderModelDefault(
    val ignoreOptions: RemainsIgnoreOptions = RemainsIgnoreOptions.of(vanilla = false, others = false),
) : IngredientRemainderModel<IngredientRemainder.Default> {

    override fun complete(): Result<IngredientRemainder.Default> {
        return Result.success(IngredientRemainder.Default.of(ignoreOptions))
    }

}

class IngredientRemainderModelCustom(
    val ignoreOptions: RemainsIgnoreOptions = RemainsIgnoreOptions.of(vanilla = false, others = false),
    val remainder: ItemStackRef = ItemStackRef.create(ItemStack.EMPTY.wrap()),
) : IngredientRemainderModel<IngredientRemainder.Custom> {

    override fun complete(): Result<IngredientRemainder.Custom> {
        return Result.success(IngredientRemainder.Custom.of(ignoreOptions, remainder))
    }

}

class IngredientRemainderModelDefaultUIFactory :
    EditorUIFactory<IngredientRemainderModel<IngredientRemainder.Default>> {

    override val modelType = IngredientRemainderModel::class.java as Class<IngredientRemainderModel<IngredientRemainder.Default>>

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientRemainderModel<IngredientRemainder.Default> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientRemainderModel<IngredientRemainder.Default> {
        return IngredientRemainderModelDefault()
    }

    @Composable
    override fun renderUI(model: IngredientRemainderModel<IngredientRemainder.Default>) {
    }

}

class IngredientRemainderModelCustomUIFactory : EditorUIFactory<IngredientRemainderModel<IngredientRemainder.Custom>> {

    override val modelType = IngredientRemainderModel::class.java as Class<IngredientRemainderModel<IngredientRemainder.Custom>>

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientRemainderModel<IngredientRemainder.Custom> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientRemainderModel<IngredientRemainder.Custom> {
        return IngredientRemainderModelCustom()
    }

    @Composable
    override fun renderUI(model: IngredientRemainderModel<IngredientRemainder.Custom>) {
    }

}