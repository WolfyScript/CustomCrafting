package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientRemainderModels
import com.wolfyscript.customcrafting.editor.ext.EditorModelFactory
import com.wolfyscript.customcrafting.core.recipe.CustomRecipe
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.core.recipe.ingredient.of
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.minecraft.wrap
import net.minecraft.world.item.ItemStack

class IngredientConsumerConsumeModel(
    val remainder: IngredientRemainderModel<*> = IngredientRemainderModels.default.resolveOrThrow().createEmptyModel()
) : IngredientConsumerModel<IngredientConsumer.Consume> {

    override fun complete(): Result<IngredientConsumer.Consume> {
        val completedRemainder = remainder.complete().getOrElse {
            return Result.failure(IllegalStateException("Failed to create Keep Ingredient Consumer: ", it))
        }
        return Result.success(IngredientConsumer.Consume.of(completedRemainder))
    }

}

class IngredientConsumerReplaceModel(
    val replacement: ItemStackRef = ItemStackRef.parse(ItemStack.EMPTY.wrap())!!
) : IngredientConsumerModel<IngredientConsumer.Replace> {

    override fun complete(): Result<IngredientConsumer.Replace> {
        return Result.success(IngredientConsumer.Replace.of(replacement))
    }

}

class IngredientConsumerKeepModel(
    val modifier: RecipeItemModifierModel = RecipeItemModifierModelImpl()
) : IngredientConsumerModel<IngredientConsumer.Keep> {

    override fun complete(): Result<IngredientConsumer.Keep> {
        val completedModifier = modifier.complete().getOrElse {
            return Result.failure(IllegalStateException("Failed to create Keep Ingredient Consumer: ", it))
        }
        return Result.success(IngredientConsumer.Keep.of(completedModifier))
    }

}

class IngredientConsumerConsumeModelFactory : EditorModelFactory<IngredientConsumerModel<IngredientConsumer.Consume>> {

    override val modelType = IngredientConsumerModel::class.java as Class<IngredientConsumerModel<IngredientConsumer.Consume>>

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientConsumerModel<IngredientConsumer.Consume> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientConsumerModel<IngredientConsumer.Consume> {
        return IngredientConsumerConsumeModel()
    }

}

class IngredientConsumerReplaceModelFactory : EditorModelFactory<IngredientConsumerModel<IngredientConsumer.Replace>> {

    override val modelType = IngredientConsumerModel::class.java as Class<IngredientConsumerModel<IngredientConsumer.Replace>>

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientConsumerModel<IngredientConsumer.Replace> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientConsumerModel<IngredientConsumer.Replace> {
        return IngredientConsumerReplaceModel()
    }

}

class IngredientConsumerKeepModelFactory : EditorModelFactory<IngredientConsumerModel<IngredientConsumer.Keep>> {

    override val modelType = IngredientConsumerModel::class.java as Class<IngredientConsumerModel<IngredientConsumer.Keep>>

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientConsumerModel<IngredientConsumer.Keep> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientConsumerModel<IngredientConsumer.Keep> {
        return IngredientConsumerKeepModel()
    }

}