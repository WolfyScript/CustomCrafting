package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonValue
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot

interface RecipeType<T: CustomRecipe<*,*>> {

    val recipeClass: Class<T>

    val icon: ItemStackSnapshot

    fun isInstance(recipe: CustomRecipe<*,*>): Boolean

    @JsonValue
    private fun serializeValue(): String {
        return CustomCraftingRegistryTypes.recipeTypes.resolveOrThrow().getKey(this).toString()
    }

}