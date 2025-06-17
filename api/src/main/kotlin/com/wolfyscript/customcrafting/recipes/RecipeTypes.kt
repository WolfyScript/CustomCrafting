package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

object RecipeTypes {

    val crafting = create<CustomRecipeCrafting>("crafting")
    val cooking = create<CustomRecipeCooking>("cooking")
    val mixing = create<CustomRecipeMixing>("mixing")
    val repairing = create<CustomRecipeRepairing>("repairing")
    val smithing = create<CustomRecipeSmithing>("smithing")
    val stonecutting = create<CustomRecipeStonecutting>("stonecutting")
    val grinding = create<CustomRecipeGrinding>("grinding")

    private inline fun <reified T: CustomRecipe<*, *>> create(key: String) : ValueReference<RecipeType<*>, RecipeType<T>> {
        return CustomCraftingRegistryTypes.recipeTypes.key.referenced<RecipeType<*>, RecipeType<T>>(Key.customCrafting(key)).reference()
    }

}