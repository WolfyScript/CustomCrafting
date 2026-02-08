package com.wolfyscript.customcrafting.ui

import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistries
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientConsumerModels
import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientMatcherModels
import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientRemainderModels
import com.wolfyscript.customcrafting.ui.editor.IngredientConsumerCustomUIProvider
import com.wolfyscript.customcrafting.ui.editor.IngredientMatcherCustomUIProvider
import com.wolfyscript.customcrafting.ui.editor.IngredientRemainderCustomUIProvider
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem.ExactIngredientMatcherUIProvider
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem.IngredientConsumerConsumeUIProvider
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem.IngredientConsumerKeepUIProvider
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem.IngredientConsumerReplaceUIProvider
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem.IngredientRemainderUICustom
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem.IngredientRemainderUIDefault
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem.ItemIngredientMatcherUIProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.Registry
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference
import com.wolfyscript.scafall.registry.RegistrySimple

class UIRegistries : CustomCraftingRegistries {

    private val rootRegistry = RegistrySimple<Registry<*>>(CustomCraftingRegistryTypes.root)

    fun init() {
        createRegistry(UIRegistryTypes.conditions) { RegistrySimple(it) }
        createRegistry(UIRegistryTypes.recipeItemTransmuters) { RegistrySimple(it) }
        createRegistry(UIRegistryTypes.resultActions) { RegistrySimple(it) }

        createRegistry(UIRegistryTypes.ingredientConsumers) {
            RegistrySimple<IngredientConsumerCustomUIProvider<*>>(it).apply {
                register(IngredientConsumerModels.consume.key.key, IngredientConsumerConsumeUIProvider())
                register(IngredientConsumerModels.replace.key.key, IngredientConsumerReplaceUIProvider())
                register(IngredientConsumerModels.keep.key.key, IngredientConsumerKeepUIProvider())
            }
        }
        createRegistry(UIRegistryTypes.ingredientRemainders) {
            RegistrySimple<IngredientRemainderCustomUIProvider<*>>(it).apply {
                register(IngredientRemainderModels.default.key.key, IngredientRemainderUIDefault())
                register(IngredientRemainderModels.custom.key.key, IngredientRemainderUICustom())
            }
        }
        createRegistry(UIRegistryTypes.ingredientMatchers) {
            RegistrySimple<IngredientMatcherCustomUIProvider<*>>(it).apply {
                register(IngredientMatcherModels.exact.key.key, ExactIngredientMatcherUIProvider())
                register(IngredientMatcherModels.item.key.key, ItemIngredientMatcherUIProvider())
            }
        }
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