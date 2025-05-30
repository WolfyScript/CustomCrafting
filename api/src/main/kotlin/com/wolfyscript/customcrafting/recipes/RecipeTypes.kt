package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.RegistryKey

interface RecipeTypes {

    companion object {

        val crafting: RecipeType<CustomRecipeCrafting> = expect("crafting")
        val cooking: RecipeType<CustomRecipeCooking> = expect("cooking")
        val mixing: RecipeType<CustomRecipeMixing> = expect("mixing")
        val repairing: RecipeType<CustomRecipeRepairing> = expect("repairing")
        val smithing: RecipeType<CustomRecipeSmithing> = expect("smithing")
        val stonecutting: RecipeType<CustomRecipeStonecutting> = expect("stonecutting")
        val grinding: RecipeType<CustomRecipeGrinding> = expect("grinding")

        private inline fun <reified T: CustomRecipe<*,*>> expect(name: String): RecipeType<T> {
            val registryKey =
                RegistryKey.of(CustomCraftingProvider.get().registries.recipeTypes, Key.key("customcrafting", name))
            val result = registryKey.get()
            if (result.isFailure) {
                throw IllegalStateException("Could not find recipe type $name in registry", result.exceptionOrNull())
            }
            val value = result.getOrNull()
            if (value != null && value is T) {
                return value as RecipeType<T>
            }
            throw IllegalStateException("Recipe type $name is not of type ${T::class.simpleName}")
        }

    }


}