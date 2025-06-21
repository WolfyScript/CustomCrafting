package com.wolfyscript.customcrafting.registry

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.RecipeStore
import com.wolfyscript.customcrafting.editor.conditions.ConditionStore
import com.wolfyscript.customcrafting.editor.result.ResultActionStore
import com.wolfyscript.customcrafting.editor.result.TransmuterStore
import com.wolfyscript.customcrafting.recipes.conditions.Condition
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.customcrafting.recipes.ResultAction
import com.wolfyscript.customcrafting.recipes.ResultModifier
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference

/**
 * A list of all the [Registries][com.wolfyscript.scafall.registry.Registry] that are included with CustomCrafting.
 */
object CustomCraftingRegistryTypes {

    val root = Key.customCrafting("root")

    val recipeTypes = create<RecipeType<*>>("recipe_types")

    val customRecipes = create<CustomRecipe<*,*>>("recipes")

    //
    // Type Registries
    // ---------------
    // Used to store all the available types of content. Allows for easy deserialization of custom types.
    //
    val recipeConditionTypes = create<Class<out Condition>>("types/recipes/conditions")

    val resultTransmuters = create<Class<out ResultModifier.Transformation.Transmuter>>("types/recipe/result/transmuters")

    val resultActions = create<Class<out ResultAction>>("types/recipe/result/actions")

    //
    // Editor Store Registries
    // -----------------------
    // Used to store the types of content used in the editor. Usually associated with a type of the above type registries.
    //
    val recipeTypeSpecificStores = create<Class<out RecipeStore.RecipeTypeSpecificStore<*>>>("editor/types/recipe/types")

    val conditionStores = create<Class<out ConditionStore<*>>>("editor/types/recipe/conditions")

    val resultTransmuterStores = create<Class<out TransmuterStore<*>>>("editor/types/recipe/result/transmuters")

    val resultActionStores = create<Class<out ResultActionStore<*>>>("editor/types/recipe/result/actions")

    private fun <T> create(registryKey: String): RegistryReference<T> {
        return RegistryKey.of<T>(root, Key.customCrafting(registryKey)).reference { CustomCraftingProvider.get().registries }
    }

}