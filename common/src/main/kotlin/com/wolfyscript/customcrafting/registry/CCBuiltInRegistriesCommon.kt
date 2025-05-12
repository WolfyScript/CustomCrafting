package com.wolfyscript.customcrafting.registry

import com.wolfyscript.customcrafting.recipes.Condition
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.Registry
import com.wolfyscript.scafall.registry.RegistrySimple
import com.wolfyscript.scafall.registry.TypeRegistry
import com.wolfyscript.scafall.registry.TypeRegistrySimple

class CCBuiltInRegistriesCommon : CCBuiltInRegistries {

    // Type Registries
    override val recipeTypes: TypeRegistry<RecipeType<*>> = TypeRegistrySimple(Key.key("customcrafting", "types/recipes"))
    override val recipeConditionTypes: TypeRegistry<Condition> = TypeRegistrySimple(Key.key("customcrafting", "types/recipe_conditions"))

    // Value Registries
    override val customRecipes: Registry<CustomRecipe<*>> = RegistrySimple(Key.key("customcrafting", "recipes"))

}