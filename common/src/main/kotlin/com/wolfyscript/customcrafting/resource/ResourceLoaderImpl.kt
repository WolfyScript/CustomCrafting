package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.configuration.resources.ResourceSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.scafall.dependency.Dependency
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.verification.VerificationResult
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
import java.io.File

class ResourceLoaderImpl(val customCrafting: CustomCrafting, val settings: ResourceSettings, val directory: File) : ResourceLoader {

    override val destinations: List<AbstractDestination<*>> = settings.destinations.mapNotNull {
        return@mapNotNull when (it) {
            // TODO: hmmm
            is DestinationSettings.LocalDestinationSettings -> LocalDestination(customCrafting, this, it)
            is DestinationSettings.SQLDestinationSettings -> SQLDestination(customCrafting, this, it)
            else -> null
        }
    }

    val awaitingDependenciesRecipes: MutableMap<Key, LoadedRecipe> = Reference2ObjectOpenHashMap()
    val awaitingVerificationRecipes: MutableMap<Key, CustomRecipe<*,*>> = Reference2ObjectOpenHashMap()
    val invalidRecipes: MutableList<VerificationResult<CustomRecipe<*,*>>> = mutableListOf()

    fun addRecipeFrom(recipe: LoadedRecipe, destination: AbstractDestination<*>) {
        if (!destination.settings.overwriteExisting && awaitingDependenciesRecipes.containsKey(recipe.key)) {
            return
        }
        awaitingDependenciesRecipes[recipe.key] = recipe
    }

    override fun loadResources() {
        // Load resources into a temporary storage
        destinations.forEach {
            it.load()
        }

        // Check for dependencies
        val iterator = awaitingDependenciesRecipes.iterator()
        while (iterator.hasNext()) {
            val recipe = iterator.next()
            if (recipe.value.areDependenciesSatisfied()) {
                iterator.remove()
                awaitingVerificationRecipes[recipe.key] = recipe.value.recipe
            }
        }
    }

    override fun verifyResources() {
        for ((key, recipe) in awaitingVerificationRecipes) {
            // TODO: Verification
            customCrafting.registries.customRecipes.register(recipe)
        }
    }

    override fun save(recipe: CustomRecipe<*,*>) {
        for (destination in destinations) {
            if (!(destination.filter?.accepts(recipe) ?: true)) {
                continue
            }
            val result = destination.save(recipe)
            if (result.isSuccess && result.getOrNull() == true) {
                if (!destination.settings.propagateSavedResources) {
                    break
                }
            }
        }
    }

    override fun delete(recipe: CustomRecipe<*,*>) {
        for (destination in destinations) {
            if (!(destination.filter?.accepts(recipe) ?: true)) {
                continue
            }
            if (destination.settings.backup != null) {
                continue
            }
            val result = destination.delete(recipe)
            if (result.isSuccess && result.getOrNull() == true) {
                // TODO: Propagate deletion?
            }
        }
    }

    override fun createBackup() {

    }

    data class LoadedRecipe(val key: Key, val recipe: CustomRecipe<*,*>, val dependencies: List<Dependency>) {

        fun areDependenciesSatisfied(): Boolean {
            return dependencies.all { it.isAvailable }
        }

    }

}
