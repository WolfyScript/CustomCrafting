package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.editor.EditorRegistryTypes
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.ext.EditorModelFactory
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.editor.EditorModule
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [IngredientMatchers][IngredientMatcher] that exist in the Registry across all platforms.
 */
object IngredientConsumerModels {

    val consume = create<EditorModelFactory<IngredientConsumerModel<IngredientConsumer.Consume>>>("consume")
    val replace = create<EditorModelFactory<IngredientConsumerModel<IngredientConsumer.Replace>>>("replace")
    val keep = create<EditorModelFactory<IngredientConsumerModel<IngredientConsumer.Keep>>>("keep")

    private inline fun <reified T: EditorModelFactory<out IngredientConsumerModel<*>>> create(key: String) : ValueReference<EditorModelFactory<out IngredientConsumerModel<*>>, T> {
        return EditorRegistryTypes.ingredientConsumers.key
            .referenced<EditorModelFactory<out IngredientConsumerModel<*>>, T>(Key.customCrafting(key))
            .reference { EditorModule.get().registries }
    }

}