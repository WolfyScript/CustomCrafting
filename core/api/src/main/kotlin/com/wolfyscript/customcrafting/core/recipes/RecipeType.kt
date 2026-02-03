package com.wolfyscript.customcrafting.core.recipes

import com.fasterxml.jackson.annotation.JsonValue
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot

interface RecipeType<T: CustomRecipe<*,*>> {

    val recipeClass: Class<T>

    val icon: ItemStackSnapshot

    fun isInstance(recipe: CustomRecipe<*,*>): Boolean

    @JsonValue
    private fun serializeValue(): String {
        return _root_ide_package_.com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.recipeTypes.resolveOrThrow().getKey(this).toString()
    }

}