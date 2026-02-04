package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.editor.EditorRegistryTypes
import com.wolfyscript.customcrafting.core.recipes.CustomRecipe
import com.wolfyscript.customcrafting.core.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.editor.EditorModule
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

object RecipeTypeSpecificStateFactories {

    val crafting = create<CustomRecipeCrafting>("crafting")
    val cooking = create<CustomRecipeCrafting>("cooking")
    val mixing = create<CustomRecipeCrafting>("mixing")
    val repairing = create<CustomRecipeCrafting>("repairing")
    val smithing = create<CustomRecipeCrafting>("smithing")
    val stonecutting = create<CustomRecipeCrafting>("stonecutting")
    val grinding = create<CustomRecipeCrafting>("grinding")

    private inline fun <reified T : CustomRecipe<*, *>> create(key: String): ValueReference<RecipeModel.RecipeTypeSpecificModel.Factory<*>, RecipeModel.RecipeTypeSpecificModel.Factory<T>> {
        return EditorRegistryTypes.recipeTypeSpecificModelFactories.key
            .referenced<RecipeModel.RecipeTypeSpecificModel.Factory<*>, RecipeModel.RecipeTypeSpecificModel.Factory<T>>(
                Key.customCrafting(key)
            ).reference { EditorModule.get().registries }
    }

}