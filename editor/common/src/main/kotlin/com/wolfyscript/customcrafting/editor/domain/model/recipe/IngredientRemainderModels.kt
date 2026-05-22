package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.editor.EditorRegistryTypes
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModel
import com.wolfyscript.customcrafting.editor.ext.EditorModelFactory
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.editor.EditorModule
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

object IngredientRemainderModels {

    val default = create<EditorModelFactory<IngredientRemainderModel<IngredientRemainder.Default>>>("default")
    val custom = create<EditorModelFactory<IngredientRemainderModel<IngredientRemainder.Custom>>>("custom")

    private inline fun <reified T: EditorModelFactory<out IngredientRemainderModel<*>>> create(key: String) : ValueReference<EditorModelFactory<out IngredientRemainderModel<*>>, T> {
        return EditorRegistryTypes.ingredientRemainders.key
            .referenced<EditorModelFactory<out IngredientRemainderModel<*>>, T>(Key.customCrafting(key))
            .reference { EditorModule.get().registries }
    }

}