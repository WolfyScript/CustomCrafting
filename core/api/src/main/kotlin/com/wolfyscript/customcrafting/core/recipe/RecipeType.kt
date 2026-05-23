package com.wolfyscript.customcrafting.core.recipe

import com.fasterxml.jackson.annotation.JsonValue
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import net.minecraft.world.item.Item

interface RecipeType<T: CustomRecipe<*,*>> {

    val recipeClass: Class<T>

    val icon: Item

    fun isInstance(recipe: CustomRecipe<*,*>): Boolean

    @JsonValue
    private fun serializeValue(): String {
        return CustomCraftingRegistryTypes.recipeTypes.resolveOrThrow().getKey(this).toString()
    }

}