package com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

object IngredientRemainderModels {

    val default = create<EditorUIFactory<IngredientRemainderModel<IngredientRemainder.Default>>>("default")
    val custom = create<EditorUIFactory<IngredientRemainderModel<IngredientRemainder.Custom>>>("custom")

    private inline fun <reified T: EditorUIFactory<out IngredientRemainderModel<*>>> create(key: String) : ValueReference<EditorUIFactory<out IngredientRemainderModel<*>>, T> {
        return CustomCraftingRegistryTypes.ingredientRemainderModels.key
            .referenced<EditorUIFactory<out IngredientRemainderModel<*>>, T>(Key.customCrafting(key))
            .reference { CustomCraftingProvider.get().registries }
    }

}