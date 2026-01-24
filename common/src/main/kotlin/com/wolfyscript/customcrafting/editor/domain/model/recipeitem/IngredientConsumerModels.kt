package com.wolfyscript.customcrafting.editor.domain.model.recipeitem

import androidx.compose.runtime.Composable
import com.wolfyscript.customcrafting.editor.domain.model.RecipeItemModifierModelImpl
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientRemainderModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientRemainderModels
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.RecipeItemModifierModel
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.IngredientConsumerKeepImpl
import com.wolfyscript.customcrafting.recipes.IngredientConsumerReplaceImpl
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumer
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.wrap
import net.minecraft.world.item.ItemStack

class IngredientConsumerConsumeModel(
    val remainder: IngredientRemainderModel<*> = IngredientRemainderModels.default.resolveOrThrow().createEmptyModel()
) : IngredientConsumerModel<IngredientConsumer.Consume> {

    override fun complete(): Result<IngredientConsumer.Consume> {
        TODO("Not yet implemented")
    }

}

class IngredientConsumerReplaceModel(
    val replacement: ItemStackRef = ItemStackRef.parse(ItemStack.EMPTY.wrap())!!
) : IngredientConsumerModel<IngredientConsumer.Replace> {

    override fun complete(): Result<IngredientConsumer.Replace> {
        return Result.success(IngredientConsumerReplaceImpl(replacement))
    }

}

class IngredientConsumerKeepModel(
    val modifier: RecipeItemModifierModel = RecipeItemModifierModelImpl()
) : IngredientConsumerModel<IngredientConsumer.Keep> {

    override fun complete(): Result<IngredientConsumer.Keep> {
        val completedModifier = modifier

        return Result.success(IngredientConsumerKeepImpl())
    }

}

class IngredientConsumerConsumeUIFactory() : EditorUIFactory<IngredientConsumerModel<IngredientConsumer.Consume>> {

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientConsumerModel<IngredientConsumer.Consume> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientConsumerModel<IngredientConsumer.Consume> {
        return IngredientConsumerConsumeModel()
    }

    @Composable
    override fun renderUI() {}

}

class IngredientConsumerReplaceUIFactory : EditorUIFactory<IngredientConsumerModel<IngredientConsumer.Replace>> {

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientConsumerModel<IngredientConsumer.Replace> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientConsumerModel<IngredientConsumer.Replace> {
        return IngredientConsumerReplaceModel()
    }

    @Composable
    override fun renderUI() {}

}

class IngredientConsumerKeepUIFactory : EditorUIFactory<IngredientConsumerModel<IngredientConsumer.Keep>> {

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientConsumerModel<IngredientConsumer.Keep> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientConsumerModel<IngredientConsumer.Keep> {
        return IngredientConsumerKeepModel()
    }

    @Composable
    override fun renderUI() {}

}