package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.EditorRegistryTypes
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [IngredientMatchers][IngredientMatcher] that exist in the Registry across all platforms.
 */
object IngredientConsumerModels {

    val consume = create<EditorUIFactory<IngredientConsumerModel<IngredientConsumer.Consume>>>("consume")
    val replace = create<EditorUIFactory<IngredientConsumerModel<IngredientConsumer.Replace>>>("replace")
    val keep = create<EditorUIFactory<IngredientConsumerModel<IngredientConsumer.Keep>>>("keep")

    private inline fun <reified T: EditorUIFactory<out IngredientConsumerModel<*>>> create(key: String) : ValueReference<EditorUIFactory<out IngredientConsumerModel<*>>, T> {
        return EditorRegistryTypes.ingredientConsumers.key
            .referenced<EditorUIFactory<out IngredientConsumerModel<*>>, T>(Key.customCrafting(key))
            .reference { CustomCraftingProvider.get().registries }
    }

}