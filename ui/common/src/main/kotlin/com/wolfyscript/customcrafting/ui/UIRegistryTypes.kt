package com.wolfyscript.customcrafting.ui

import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.root
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.ui.editor.*
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference

object UIRegistryTypes {

    val conditions = create<RecipeConditionCustomUIProvider<*, *>>("ui/recipe/conditions")

    val ingredientMatchers = create<IngredientMatcherCustomUIProvider<*>>("ui/recipe/ingredient/matchers")

    val ingredientConsumers = create<IngredientConsumerCustomUIProvider<*>>("ui/recipe/ingredient/consumers")

    val ingredientRemainders = create<IngredientRemainderCustomUIProvider<*>>("ui/recipe/ingredient/remainders")

    val recipeItemTransmuters = create<RecipeItemTransmuterCustomUIProvider<*, *>>("ui/recipe/item/transmuters")

    val resultActions = create<RecipeResultActionCustomUIProvider<*, *>>("ui/recipe/result/actions")

    private fun <T> create(registryKey: String): RegistryReference<T> {
        return RegistryKey.of<T>(root, Key.customCrafting(registryKey)).reference { UIModule.get().registries }
    }

}