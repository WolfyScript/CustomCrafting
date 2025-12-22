package com.wolfyscript.customcrafting.registry

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.model.recipes.RecipeState
import com.wolfyscript.customcrafting.editor.model.recipes.conditions.ConditionStore
import com.wolfyscript.customcrafting.editor.model.recipes.result.ResultActionState
import com.wolfyscript.customcrafting.editor.model.recipes.result.TransmuterStore
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.recipes.conditions.Condition
import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.customcrafting.recipes.ResultAction
import com.wolfyscript.customcrafting.recipes.RecipeItemModifier
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference

/**
 * A list of all the [Registries][com.wolfyscript.scafall.registry.Registry] that are included with CustomCrafting.
 */
object CustomCraftingRegistryTypes {

    val root = Key.customCrafting("root")

    /**
     * [RecipeTypes][com.wolfyscript.customcrafting.recipes.RecipeTypes]
     */
    val recipeTypes = create<RecipeType<*>>("recipe_types")

    //
    // Type Registries
    // ---------------
    // Used to store all the available types of content. Allows for easy deserialization of custom types.
    //
    /**
     * [RecipeConditions][com.wolfyscript.customcrafting.recipes.RecipeConditions]
     */
    val recipeConditionTypes = create<Class<out Condition>>("types/recipes/conditions")

    /**
     * [RecipeItemTransmuters][com.wolfyscript.customcrafting.recipes.RecipeItemTransmuters]
     */
    val recipeItemTransmuters = create<Class<out RecipeItemModifier.Transformation.Transmuter>>("types/recipe/item/transmuters")

    /**
     * [ResultActions][com.wolfyscript.customcrafting.recipes.ResultActions]
     */
    val resultActions = create<Class<out ResultAction>>("types/recipe/result/actions")

    /**
     * [IngredientConsumers][com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumers]
     */
    val ingredientConsumers = create<Class<out IngredientConsumer>>("types/recipe/ingredient/consumers")

    /**
     * [IngredientMatchers][com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatchers]
     */
    val ingredientMatchers = create<Class<out IngredientMatcher>>("types/recipe/ingredient/matchers")

    /**
     * [IngredientRemainders][com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainders]
     */
    val ingredientRemainders = create<Class<out IngredientRemainder>>("types/recipe/ingredient/remainders")

    //
    // Editor Store Registries
    // -----------------------
    // Used to store the types of content used in the editor. Usually associated with a type of the above type registries.
    //

    val recipeTypeSpecificStateFactories = create<RecipeState.RecipeTypeSpecificState.Factory<*>>("editor/recipe/factories")

    val conditionStores = create<Class<out ConditionStore<*>>>("editor/recipe/conditions")

    val recipeItemTransmuterStores = create<Class<out TransmuterStore<*>>>("editor/recipe/item/transmuters")

    val resultActionStores = create<Class<out ResultActionState<*>>>("editor/recipe/result/actions")

    private fun <T> create(registryKey: String): RegistryReference<T> {
        return RegistryKey.of<T>(root, Key.customCrafting(registryKey)).reference { CustomCraftingProvider.get().registries }
    }

}