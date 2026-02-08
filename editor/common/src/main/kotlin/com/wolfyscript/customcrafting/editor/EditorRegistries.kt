package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientConsumerModels
import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientMatcherModels
import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientRemainderModels
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeCraftingModelFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeTypeSpecificStateFactories
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ExactIngredientMatcherModelFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerConsumeModelFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerKeepModelFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerReplaceModelFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModelCustomModelFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModelDefaultModelFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ItemIngredientMatcherModelFactory
import com.wolfyscript.customcrafting.editor.ext.EditorModelFactory
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistries
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.Registry
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference
import com.wolfyscript.scafall.registry.RegistrySimple

internal class EditorRegistries : CustomCraftingRegistries {

    private val rootRegistry = RegistrySimple<Registry<*>>(CustomCraftingRegistryTypes.root)

    fun initRegistries() {
        createRegistry(EditorRegistryTypes.recipeTypeSpecificModelFactories) {
            RegistrySimple<RecipeModel.RecipeTypeSpecificModel.Factory<*>>(it).apply {
                register(RecipeTypeSpecificStateFactories.crafting.key.key, RecipeCraftingModelFactory())
            }
        }
        createRegistry(EditorRegistryTypes.conditions) { RegistrySimple(it) }
        createRegistry(EditorRegistryTypes.recipeItemTransmuters) { RegistrySimple(it) }
        createRegistry(EditorRegistryTypes.resultActions) { RegistrySimple(it) }

        createRegistry(EditorRegistryTypes.ingredientConsumers) {
            RegistrySimple<EditorModelFactory<out IngredientConsumerModel<*>>>(it).apply {
                register(IngredientConsumerModels.consume.key.key, IngredientConsumerConsumeModelFactory())
                register(IngredientConsumerModels.replace.key.key, IngredientConsumerReplaceModelFactory())
                register(IngredientConsumerModels.keep.key.key, IngredientConsumerKeepModelFactory())
            }
        }
        createRegistry(EditorRegistryTypes.ingredientRemainders) {
            RegistrySimple<EditorModelFactory<out IngredientRemainderModel<*>>>(it).apply {
                register(IngredientRemainderModels.default.key.key, IngredientRemainderModelDefaultModelFactory())
                register(IngredientRemainderModels.custom.key.key, IngredientRemainderModelCustomModelFactory())
            }
        }
        createRegistry(EditorRegistryTypes.ingredientMatchers) {
            RegistrySimple<EditorModelFactory<out IngredientMatcherModel<*>>>(it).apply {
                register(IngredientMatcherModels.exact.key.key, ExactIngredientMatcherModelFactory())
                register(IngredientMatcherModels.item.key.key, ItemIngredientMatcherModelFactory())
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