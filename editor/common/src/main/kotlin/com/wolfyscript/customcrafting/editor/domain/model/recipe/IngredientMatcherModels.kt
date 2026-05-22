package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.editor.EditorRegistryTypes
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherModel
import com.wolfyscript.customcrafting.editor.ext.EditorModelFactory
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.editor.EditorModule
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [IngredientMatchers][IngredientMatcher] that exist in the Registry across all platforms.
 */
object IngredientMatcherModels {

    val item = create<EditorModelFactory<IngredientMatcherModel<IngredientMatcher.Item>>>("item")
    val exact = create<EditorModelFactory<IngredientMatcherModel<IngredientMatcher.Exact>>>("exact")

    private inline fun <reified T: EditorModelFactory<out IngredientMatcherModel<*>>> create(key: String) : ValueReference<EditorModelFactory<out IngredientMatcherModel<*>>, T> {
        return EditorRegistryTypes.ingredientMatchers.key
            .referenced<EditorModelFactory<out IngredientMatcherModel<*>>, T>(Key.customCrafting(key))
            .reference { EditorModule.get().registries }
    }

}