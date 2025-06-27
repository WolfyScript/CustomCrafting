package com.wolfyscript.customcrafting.registry

import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.recipes.actions.CommandResultAction
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.Registry
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference
import com.wolfyscript.scafall.registry.RegistrySimple

class CustomCraftingRegistriesCommon : CustomCraftingRegistries {

    private val rootRegistry = RegistrySimple<Registry<*>>(CustomCraftingRegistryTypes.root)

    fun initRegistries() {
        createRegistry(CustomCraftingRegistryTypes.recipeTypes) {
            RegistrySimple<RecipeType<*>>(it).apply {
                register(RecipeTypes.crafting.key.key, RecipeTypeImpl(CustomRecipeCrafting::class.java))
                register(RecipeTypes.cooking.key.key, RecipeTypeImpl(CustomRecipeCooking::class.java))
                register(RecipeTypes.mixing.key.key, RecipeTypeImpl(CustomRecipeMixing::class.java))
                register(RecipeTypes.repairing.key.key, RecipeTypeImpl(CustomRecipeRepairing::class.java))
                register(RecipeTypes.smithing.key.key, RecipeTypeImpl(CustomRecipeSmithing::class.java))
                register(RecipeTypes.stonecutting.key.key, RecipeTypeImpl(CustomRecipeStonecutting::class.java))
                register(RecipeTypes.grinding.key.key, RecipeTypeImpl(CustomRecipeGrinding::class.java))
            }
        }

        createRegistry(CustomCraftingRegistryTypes.recipeConditionTypes) { RegistrySimple(it) }
        createRegistry(CustomCraftingRegistryTypes.resultTransmuters) { RegistrySimple(it) }
        createRegistry(CustomCraftingRegistryTypes.resultActions) {
            RegistrySimple<Class<out ResultAction>>(it).apply {
                register(ResultActions.command.key.key, CommandResultAction::class.java)
            }
        }

        createRegistry(CustomCraftingRegistryTypes.recipeTypeSpecificStores) { RegistrySimple(it) }
        createRegistry(CustomCraftingRegistryTypes.conditionStores) { RegistrySimple(it) }
        createRegistry(CustomCraftingRegistryTypes.resultTransmuterStores) { RegistrySimple(it) }
        createRegistry(CustomCraftingRegistryTypes.resultActionStores) { RegistrySimple(it) }

        registerJacksonTypes()
    }

    fun registerJacksonTypes() {
        RegistryKeyTypeIdResolver.registerTypeRegistry(
            ResultAction::class.java,
            get(CustomCraftingRegistryTypes.resultActions.key).getOrThrow()
        )

    }

    fun <T> createRegistry(type: RegistryReference<T>, loader: (key: Key) -> Registry<T>) {
        val registry = loader(type.key.registry)
        rootRegistry.register(type.key.registry, registry)
    }

    override fun <T> get(type: RegistryKey<T>): Result<Registry<T>> {
        val registry = rootRegistry[type.registry]
        if (registry == null) {
            return Result.failure(IllegalArgumentException("No registry found for ${type.registry}"))
        }
        return Result.success(registry as Registry<T>)
    }

}