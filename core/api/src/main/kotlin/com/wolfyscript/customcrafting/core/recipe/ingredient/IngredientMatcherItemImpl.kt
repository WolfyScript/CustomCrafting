package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStackLike
import net.minecraft.core.registries.BuiltInRegistries

internal class IngredientMatcherItemImpl(
    override val mustContain: Set<Key> = emptySet(),
    override val mustNotContain: Set<Key> = emptySet(),
) : IngredientMatcher.Item {

    override fun match(
        ingredient: Ingredient,
        source: ItemStackLike,
    ): ItemStackRef? {
        return ingredient.choices.all().firstOrNull { choice ->
            if (!choice.matches(source, false)) {
                return@firstOrNull false
            }
            val mcStack = source.unwrap()
            if (mustContain.isNotEmpty() && mustContain.any { key ->
                    !BuiltInRegistries.DATA_COMPONENT_TYPE.get(key.toMc())
                        .map { mcStack.hasNonDefault(it.value()) }
                        .orElse(true)
                }) {
                return@firstOrNull false
            }
            if (mustNotContain.isNotEmpty() && mustNotContain.any { key ->
                    BuiltInRegistries.DATA_COMPONENT_TYPE.get(key.toMc())
                        .map { mcStack.hasNonDefault(it.value()) }
                        .orElse(false)
                }) {
                return@firstOrNull false
            }
            return@firstOrNull true
        }
    }

    override fun toString(): String {
        return "(mustContain=$mustContain, mustNotContain=$mustNotContain)"
    }

}