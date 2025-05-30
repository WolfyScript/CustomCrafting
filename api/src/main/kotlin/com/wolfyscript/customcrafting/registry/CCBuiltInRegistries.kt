package com.wolfyscript.customcrafting.registry

import com.wolfyscript.customcrafting.editor.RecipeStore
import com.wolfyscript.customcrafting.editor.conditions.ConditionStore
import com.wolfyscript.customcrafting.editor.result.ResultActionStore
import com.wolfyscript.customcrafting.editor.result.TransmuterStore
import com.wolfyscript.customcrafting.recipes.Condition
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.customcrafting.recipes.ResultAction
import com.wolfyscript.customcrafting.recipes.ResultModifier
import com.wolfyscript.scafall.registry.Registry
import com.wolfyscript.scafall.registry.TypeRegistry

interface CCBuiltInRegistries {

    val recipeTypes: Registry<RecipeType<*>>

    val customRecipes: Registry<CustomRecipe<*,*>>

    //
    // Type Registries
    // ---------------
    // Used to store all the available types of content. Allows for easy deserialization of custom types.
    //
    val recipeConditionTypes: TypeRegistry<Condition>

    val resultTransmuters: TypeRegistry<ResultModifier.Transformation.Transmuter>

    val resultActions: TypeRegistry<ResultAction>

    //
    // Editor Store Registries
    // -----------------------
    // Used to store the types of content used in the editor. Usually associated with a type of the above type registries.
    //
    val recipeTypeSpecificStores: TypeRegistry<RecipeStore.RecipeTypeSpecificStore<*>>

    val conditionStores: TypeRegistry<ConditionStore<*>>

    val resultTransmuterStores: TypeRegistry<TransmuterStore<*>>

    val resultActionStores: TypeRegistry<ResultActionStore<*>>


}