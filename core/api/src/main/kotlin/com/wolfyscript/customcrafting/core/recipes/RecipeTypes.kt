package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * A list of all the default [RecipeTypes][RecipeType] that exist across all platforms.
 *
 * Some platforms or third-parties may register other types, but those won't be listed here.
 */
object RecipeTypes {

    val crafting = create<CustomRecipeCrafting>("crafting")
    val cooking = create<CustomRecipeCooking>("cooking")
    val mixing = create<CustomRecipeMixing>("mixing")
    val repairing = create<CustomRecipeRepairing>("repairing")
    val smithing = create<CustomRecipeSmithing>("smithing")
    val stonecutting = create<CustomRecipeStonecutting>("stonecutting")
    val grinding = create<CustomRecipeGrinding>("grinding")

    private inline fun <reified T: CustomRecipe<*, *>> create(key: String) : ValueReference<RecipeType<*>, RecipeType<T>> {
        return _root_ide_package_.com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.recipeTypes.key.referenced<RecipeType<*>, RecipeType<T>>(
            _root_ide_package_.com.wolfyscript.scafall.identifier.Key.Companion.customCrafting(key)).reference { CustomCraftingProvider.get().registries }
    }

}