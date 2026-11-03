package com.wolfyscript.customcrafting.fabric.recipes.proxy

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import java.util.stream.Stream

class CustomRecipeProxyMap(
    private val byType: Multimap<RecipeType<*>, RecipeHolder<*>>,
    private val byKey: MutableMap<ResourceKey<Recipe<*>>, RecipeHolder<*>>
) {

    companion object {

        val EMPTY: CustomRecipeProxyMap = CustomRecipeProxyMap(ImmutableMultimap.of(), mutableMapOf())

        fun create(recipes: Collection<RecipeHolder<*>>): CustomRecipeProxyMap {
            val byType = ImmutableMultimap.builder<RecipeType<*>, RecipeHolder<*>>()
            val byKey = ImmutableMap.builder<ResourceKey<Recipe<*>>, RecipeHolder<*>>()

            recipes.forEach { legacyRecipeHolder ->
                byType.put(legacyRecipeHolder.value.getType(), legacyRecipeHolder)
                byKey.put(legacyRecipeHolder.id, legacyRecipeHolder)
            }
            return CustomRecipeProxyMap(byType.build(), byKey.build())
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <I : RecipeInput, T : Recipe<I>> byType(type: RecipeType<T>): Collection<RecipeHolder<T>> {
        return this.byType.get(type) as Collection<RecipeHolder<T>>
    }

    fun values(): MutableCollection<RecipeHolder<*>> {
        return this.byKey.values
    }

    fun byKey(recipeId: ResourceKey<Recipe<*>>): RecipeHolder<*>? {
        return this.byKey[recipeId]
    }

    fun <I : RecipeInput, T : Recipe<I>> getRecipesFor(
        type: RecipeType<T>,
        container: I,
        level: Level,
    ): Stream<RecipeHolder<T>> {
        return if (container.isEmpty) Stream.empty<RecipeHolder<T>>() else this.byType(type).stream()
            .filter { r: RecipeHolder<T> -> r.value().matches(container, level) }
    }


}