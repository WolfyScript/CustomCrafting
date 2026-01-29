package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.conditions.ConditionModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientRemainderModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ResultActionModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.TransmuterModel
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes.root
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference

object EditorRegistryTypes {

    //
    // Editor Model/UI Registries
    // -----------------------
    // Used to store data in the editor and render the UI for custom extensions.
    //  - When editing a recipe the data is converted into the model and it's submodels.
    //  - The editor manipulates the model and it's submodels.
    //  - When saving, a new recipe is constructed from the model and submodels.
    //    - each submodel may fail if requirements are not fulfilled, e.g. missing properties.
    //    - in which case a Result.failure is returned and propagates up the submodels producing a detailed stack of the issue.
    //

    val recipeTypeSpecificModelFactories = create<RecipeModel.RecipeTypeSpecificModel.Factory<*>>("editor/recipe/factories")

    val conditions = create<EditorUIFactory<out ConditionModel<*>>>("editor/recipe/conditions")

    val ingredientMatchers = create<EditorUIFactory<out IngredientMatcherModel<*>>>("editor/recipe/ingredient/matchers")

    val ingredientConsumers = create<EditorUIFactory<out IngredientConsumerModel<*>>>("editor/recipe/ingredient/consumers")

    val ingredientRemainders = create<EditorUIFactory<out IngredientRemainderModel<*>>>("editor/recipe/ingredient/remainders")

    val recipeItemTransmuters = create<EditorUIFactory<out TransmuterModel<*>>>("editor/recipe/item/transmuters")

    val resultActions = create<EditorUIFactory<out ResultActionModel<*>>>("editor/recipe/result/actions")

    private fun <T> create(registryKey: String): RegistryReference<T> {
        return RegistryKey.of<T>(root, Key.customCrafting(registryKey)).reference { CustomCraftingProvider.get().registries }
    }

}