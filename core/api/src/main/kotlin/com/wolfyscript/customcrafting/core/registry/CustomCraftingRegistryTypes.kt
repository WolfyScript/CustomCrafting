package com.wolfyscript.customcrafting.core.registry

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.core.recipe.condition.Condition
import com.wolfyscript.customcrafting.core.recipe.RecipeType
import com.wolfyscript.customcrafting.core.recipe.action.ResultAction
import com.wolfyscript.customcrafting.core.recipe.modifier.Transformation
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference

/**
 * A list of all the [Registries][com.wolfyscript.scafall.registry.Registry] that are included with CustomCrafting.
 */
object CustomCraftingRegistryTypes {

    val root = Key.customCrafting("root")

    /**
     * [RecipeTypes][com.wolfyscript.customcrafting.core.recipe.RecipeTypes]
     */
    val recipeTypes = create<RecipeType<*>>("recipe_types")

    //
    // Type Registries
    // ---------------
    // Used to store all the available types of content. Allows for easy deserialization of custom types.
    //
    /**
     * [RecipeConditions][com.wolfyscript.customcrafting.core.recipe.RecipeConditions]
     */
    val recipeConditionTypes = create<Class<out Condition>>("types/recipes/conditions")

    /**
     * [RecipeItemTransmuters][com.wolfyscript.customcrafting.core.recipe.RecipeItemTransmuters]
     */
    val recipeItemTransmuters = create<Class<out Transformation.Transmuter>>("types/recipe/item/transmuters")

    /**
     * [ResultActions][com.wolfyscript.customcrafting.core.recipe.ResultActions]
     */
    val resultActions = create<Class<out ResultAction>>("types/recipe/result/actions")

    /**
     * [IngredientConsumers][com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientConsumers]
     */
    val ingredientConsumers = create<Class<out IngredientConsumer>>("types/recipe/ingredient/consumers")

    /**
     * [IngredientMatchers][com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientMatchers]
     */
    val ingredientMatchers = create<Class<out IngredientMatcher>>("types/recipe/ingredient/matchers")

    /**
     * [IngredientRemainders][com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientRemainders]
     */
    val ingredientRemainders = create<Class<out IngredientRemainder>>("types/recipe/ingredient/remainders")

    private fun <T> create(registryKey: String): RegistryReference<T> {
        return RegistryKey.of<T>(root, Key.customCrafting(registryKey)).reference { CustomCraftingProvider.get().registries }
    }

}