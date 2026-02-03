package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientConsumerModels
import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientMatcherModels
import com.wolfyscript.customcrafting.editor.domain.model.recipe.IngredientRemainderModels
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeCraftingModelFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeTypeSpecificStateFactories
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ExactIngredientMatcherUIFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerConsumeUIFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerKeepUIFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerReplaceUIFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModelCustomUIFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModelDefaultUIFactory
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ItemIngredientMatcherUIFactory
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
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
            RegistrySimple<EditorUIFactory<out IngredientConsumerModel<*>>>(it).apply {
                register(IngredientConsumerModels.consume.key.key, IngredientConsumerConsumeUIFactory())
                register(IngredientConsumerModels.replace.key.key, IngredientConsumerReplaceUIFactory())
                register(IngredientConsumerModels.keep.key.key, IngredientConsumerKeepUIFactory())
            }
        }
        createRegistry(EditorRegistryTypes.ingredientRemainders) {
            RegistrySimple<EditorUIFactory<out IngredientRemainderModel<*>>>(it).apply {
                register(IngredientRemainderModels.default.key.key, IngredientRemainderModelDefaultUIFactory())
                register(IngredientRemainderModels.custom.key.key, IngredientRemainderModelCustomUIFactory())
            }
        }
        createRegistry(EditorRegistryTypes.ingredientMatchers) {
            RegistrySimple<EditorUIFactory<out IngredientMatcherModel<*>>>(it).apply {
                register(IngredientMatcherModels.exact.key.key, ExactIngredientMatcherUIFactory())
                register(IngredientMatcherModels.item.key.key, ItemIngredientMatcherUIFactory())
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