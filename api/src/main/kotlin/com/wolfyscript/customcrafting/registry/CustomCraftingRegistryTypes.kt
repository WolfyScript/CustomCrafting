package com.wolfyscript.customcrafting.registry

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeModel
import com.wolfyscript.customcrafting.editor.domain.recipes.conditions.ConditionModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientMatcherModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientRemainderModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.ResultActionModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.TransmuterModel
import com.wolfyscript.customcrafting.editor.ext.EditorUIFactory
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

    val conditionModels = create<EditorUIFactory<out ConditionModel<*>>>("editor/recipe/conditions")

    val ingredientMatcherModels = create<EditorUIFactory<out IngredientMatcherModel<*>>>("editor/recipe/ingredient/matchers")

    val ingredientConsumerModels = create<EditorUIFactory<out IngredientConsumerModel<*>>>("editor/recipe/ingredient/consumers")

    val ingredientRemainderModels = create<EditorUIFactory<out IngredientRemainderModel<*>>>("editor/recipe/ingredient/remainders")

    val recipeItemTransmuterModels = create<EditorUIFactory<out TransmuterModel<*>>>("editor/recipe/item/transmuters")

    val resultActionModel = create<EditorUIFactory<out ResultActionModel<*>>>("editor/recipe/result/actions")

    private fun <T> create(registryKey: String): RegistryReference<T> {
        return RegistryKey.of<T>(root, Key.customCrafting(registryKey)).reference { CustomCraftingProvider.get().registries }
    }

}