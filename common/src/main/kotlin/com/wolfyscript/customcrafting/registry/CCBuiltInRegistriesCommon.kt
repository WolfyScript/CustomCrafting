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
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.Registry
import com.wolfyscript.scafall.registry.RegistrySimple
import com.wolfyscript.scafall.registry.TypeRegistry
import com.wolfyscript.scafall.registry.TypeRegistrySimple

class CCBuiltInRegistriesCommon : CCBuiltInRegistries {

    // Type Registries
    override val recipeConditionTypes: TypeRegistry<Condition> = TypeRegistrySimple(Key.key("customcrafting", "types/recipe/conditions"))
    override val resultTransmuters: TypeRegistry<ResultModifier.Transformation.Transmuter> = TypeRegistrySimple(Key.key("customcrafting", "types/recipe/result/transmuters"))
    override val resultActions: TypeRegistry<ResultAction> = TypeRegistrySimple(Key.key("customcrafting", "types/recipe/result/actions"))

    // Editor Store Registries
    override val recipeTypeSpecificStores: TypeRegistry<RecipeStore.RecipeTypeSpecificStore<*>> = TypeRegistrySimple(Key.key("customcrafting", "types/editor/recipe/types"))
    override val conditionStores: TypeRegistry<ConditionStore<*>> = TypeRegistrySimple(Key.key("customcrafting", "types/editor/recipe/conditions"))
    override val resultTransmuterStores: TypeRegistry<TransmuterStore<*>> = TypeRegistrySimple(Key.key("customcrafting", "types/editor/recipe/result/transmuters"))
    override val resultActionStores: TypeRegistry<ResultActionStore<*>> = TypeRegistrySimple(Key.key("customcrafting", "types/editor/recipe/result/actions"))

    // Value Registries
    override val recipeTypes: Registry<RecipeType<*>> = RegistrySimple(Key.key("customcrafting", "recipe_types"))
    override val customRecipes: Registry<CustomRecipe<*,*>> = RegistrySimple(Key.key("customcrafting", "recipes"))

}