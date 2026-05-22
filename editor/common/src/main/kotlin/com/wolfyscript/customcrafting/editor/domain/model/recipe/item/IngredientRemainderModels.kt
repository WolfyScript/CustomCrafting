package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.editor.ext.EditorModelFactory
import com.wolfyscript.customcrafting.core.recipe.*
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.core.recipe.ingredient.of
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.minecraft.wrap
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

class IngredientRemainderModelDefaultModelFactory :
    EditorModelFactory<IngredientRemainderModel<IngredientRemainder.Default>> {

    override val modelType = IngredientRemainderModel::class.java as Class<IngredientRemainderModel<IngredientRemainder.Default>>

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientRemainderModel<IngredientRemainder.Default> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientRemainderModel<IngredientRemainder.Default> {
        return IngredientRemainderModelDefault()
    }

}

class IngredientRemainderModelCustomModelFactory : EditorModelFactory<IngredientRemainderModel<IngredientRemainder.Custom>> {

    override val modelType = IngredientRemainderModel::class.java as Class<IngredientRemainderModel<IngredientRemainder.Custom>>

    override fun loadIntoModel(recipe: CustomRecipe<*, *>): IngredientRemainderModel<IngredientRemainder.Custom> {
        TODO("Not yet implemented")
    }

    override fun createEmptyModel(): IngredientRemainderModel<IngredientRemainder.Custom> {
        return IngredientRemainderModelCustom()
    }

}