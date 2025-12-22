package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.CustomRecipeCrafting
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.util.customCrafting
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

    private inline fun <reified T : CustomRecipe<*, *>> create(key: String): ValueReference<RecipeState.RecipeTypeSpecificState.Factory<*>, RecipeState.RecipeTypeSpecificState.Factory<T>> {
        return CustomCraftingRegistryTypes.recipeTypeSpecificStateFactories.key
            .referenced<RecipeState.RecipeTypeSpecificState.Factory<*>, RecipeState.RecipeTypeSpecificState.Factory<T>>(
                Key.customCrafting(key)
            ).reference { CustomCraftingProvider.get().registries }
    }

}