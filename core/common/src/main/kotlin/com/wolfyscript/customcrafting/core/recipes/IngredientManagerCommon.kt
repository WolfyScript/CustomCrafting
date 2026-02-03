package com.wolfyscript.customcrafting.core.recipes

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientManager.Companion.LOG_PREFIX
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientManager
import com.wolfyscript.customcrafting.core.resource.DataType
import com.wolfyscript.customcrafting.core.resource.LoadedObject
import com.wolfyscript.customcrafting.core.resource.ResourceLoader
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

class IngredientManagerCommon(val customCrafting: CustomCrafting) : IngredientManager {

    private val ingredients: BiMap<Key, Ingredient> = HashBiMap.create()
    private val ingredientsLoadedByCC: MutableSet<Key> = ObjectOpenHashSet()

    private val ingredientsAwaitingDependencies: MutableList<LoadedObject<Ingredient>> = mutableListOf()

    val scafall = ScafallProvider.get()

    init {
        scafall.dependencyManager.onDependencyInitialized {
            verifyIngredientsAndLoad()
        }
    }

    override fun registerIngredient(
        key: Key,
        ingredient: Ingredient,
    ) {
        if (ingredients.containsKey(key)) {
            error("Ingredient with key $key already exists.")
        }
        if (ingredients.containsValue(ingredient)) {
            error("Ingredient $ingredient is already registered under ${ingredients.inverse()[ingredient]}.")
        }
        this.ingredients[key] = ingredient
    }

    override fun getIngredient(key: Key): Ingredient? {
        return this.ingredients[key]
    }

    override fun getKey(ingredient: Ingredient): Key? {
        return this.ingredients.inverse()[ingredient]
    }

    override fun onInitialLoad(resourceLoader: ResourceLoader) {
        resourceLoader.sources.forEach { source ->
            source.load(DataType.Ingredients) {
                customCrafting.logger.info("${LOG_PREFIX}loaded: ${it.key} -> ${it.value}")
                ingredientsAwaitingDependencies.add(it)
            }
        }
    }

    override fun onReload(resourceLoader: ResourceLoader) {
        // TODO
    }

    override fun onFinalize(resourceLoader: ResourceLoader) {
        val previouslyLoaded = this.ingredientsLoadedByCC
        this.ingredientsLoadedByCC.clear()

        verifyIngredientsAndLoad()

        val removed = previouslyLoaded.subtract(ingredientsLoadedByCC)
        if (removed.isNotEmpty()) {
            customCrafting.logger.info("${LOG_PREFIX}Removing ${removed.size} ingredients that are no longer loaded")
            removed.forEach { ingredients.remove(it) }
        }
    }

    private fun verifyIngredientsAndLoad() {
        customCrafting.logger.info("${LOG_PREFIX}Registering ${ingredientsAwaitingDependencies.size} ingredients")
        for (loadedIngredient in ingredientsAwaitingDependencies) {
            registerIngredient(loadedIngredient.key, loadedIngredient.value)
            ingredientsLoadedByCC.add(loadedIngredient.key)
        }
    }

}