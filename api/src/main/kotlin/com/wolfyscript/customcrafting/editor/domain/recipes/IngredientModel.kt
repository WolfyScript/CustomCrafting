package com.wolfyscript.customcrafting.editor.domain.recipes

import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientConsumerModel
import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.IngredientMatcherModel
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot

interface IngredientModel {

    fun complete(): Result<Ingredient>

    interface CustomIngredientModel : IngredientModel {

        val choices: RecipeChoicesModel

        val matcher: IngredientMatcherModel<*>

        val consumer: IngredientConsumerModel<*>

        val replaceWithRemains: Boolean

    }

    interface SavedIngredientModel : IngredientModel {

        val key: Key

        val icon: ItemStackSnapshot

    }

}