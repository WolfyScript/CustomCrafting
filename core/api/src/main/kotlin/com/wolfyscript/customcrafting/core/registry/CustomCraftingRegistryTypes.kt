package com.wolfyscript.customcrafting.core.registry

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.core.recipes.conditions.Condition
import com.wolfyscript.customcrafting.core.recipes.RecipeType
import com.wolfyscript.customcrafting.core.recipes.ResultAction
import com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference

/**
 * A list of all the [Registries][com.wolfyscript.scafall.registry.Registry] that are included with CustomCrafting.
 */
object CustomCraftingRegistryTypes {

    val root = _root_ide_package_.com.wolfyscript.scafall.identifier.Key.Companion.customCrafting("root")

    /**
     * [RecipeTypes][com.wolfyscript.customcrafting.core.recipes.RecipeTypes]
     */
    val recipeTypes = create<com.wolfyscript.customcrafting.core.recipes.RecipeType<*>>("recipe_types")

    //
    // Type Registries
    // ---------------
    // Used to store all the available types of content. Allows for easy deserialization of custom types.
    //
    /**
     * [RecipeConditions][com.wolfyscript.customcrafting.core.recipes.RecipeConditions]
     */
    val recipeConditionTypes = create<Class<out com.wolfyscript.customcrafting.core.recipes.conditions.Condition>>("types/recipes/conditions")

    /**
     * [RecipeItemTransmuters][com.wolfyscript.customcrafting.core.recipes.RecipeItemTransmuters]
     */
    val recipeItemTransmuters = create<Class<out com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier.Transformation.Transmuter>>("types/recipe/item/transmuters")

    /**
     * [ResultActions][com.wolfyscript.customcrafting.core.recipes.ResultActions]
     */
    val resultActions = create<Class<out com.wolfyscript.customcrafting.core.recipes.ResultAction>>("types/recipe/result/actions")

    /**
     * [IngredientConsumers][com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientConsumers]
     */
    val ingredientConsumers = create<Class<out com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientConsumer>>("types/recipe/ingredient/consumers")

    /**
     * [IngredientMatchers][com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatchers]
     */
    val ingredientMatchers = create<Class<out com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatcher>>("types/recipe/ingredient/matchers")

    /**
     * [IngredientRemainders][com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientRemainders]
     */
    val ingredientRemainders = create<Class<out com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientRemainder>>("types/recipe/ingredient/remainders")

    private fun <T> create(registryKey: String): RegistryReference<T> {
        return RegistryKey.of<T>(root, Key.customCrafting(registryKey)).reference { CustomCraftingProvider.get().registries }
    }

}