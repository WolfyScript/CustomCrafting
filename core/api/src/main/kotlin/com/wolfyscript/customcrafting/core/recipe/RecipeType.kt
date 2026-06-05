package com.wolfyscript.customcrafting.core.recipe

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import net.minecraft.world.item.Item

/**
 * Represents a type of custom recipe.
 *
 * @param T the type of custom recipe this type represents.
 */
@JsonDeserialize(`as` = RecipeTypeImpl::class)
interface RecipeType<T: CustomRecipe<*,*>> {

    /**
     * The class of the custom recipe this type represents.
     */
    val recipeClass: Class<T>

    /**
     * The icon item for this recipe type.
     */
    val icon: Item

    /**
     * Checks if the given recipe is an instance of this recipe type.
     *
     * @param recipe the recipe to check.
     * @return true if the recipe is an instance of this recipe type, false otherwise.
     */
    fun isInstance(recipe: CustomRecipe<*,*>): Boolean

    /**
     * Serializes the value of this recipe type for JSON serialization.
     *
     * @return the serialized value of this recipe type.
     */
    @JsonValue
    private fun serializeValue(): String {
        return CustomCraftingRegistryTypes.recipeTypes.resolveOrThrow().getKey(this).toString()
    }

}