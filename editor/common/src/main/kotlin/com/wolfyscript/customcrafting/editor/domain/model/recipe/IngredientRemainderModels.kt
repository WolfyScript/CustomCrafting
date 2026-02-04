package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.editor.EditorRegistryTypes
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModel
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.editor.EditorModule
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

object IngredientRemainderModels {

    val default = create<EditorUIFactory<IngredientRemainderModel<IngredientRemainder.Default>>>("default")
    val custom = create<EditorUIFactory<IngredientRemainderModel<IngredientRemainder.Custom>>>("custom")

    private inline fun <reified T: EditorUIFactory<out IngredientRemainderModel<*>>> create(key: String) : ValueReference<EditorUIFactory<out IngredientRemainderModel<*>>, T> {
        return EditorRegistryTypes.ingredientRemainders.key
            .referenced<EditorUIFactory<out IngredientRemainderModel<*>>, T>(Key.customCrafting(key))
            .reference { EditorModule.get().registries }
    }

}