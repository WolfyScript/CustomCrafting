package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStackLike

/**
 * Defines how a source stack is matched against an [Ingredient].
 *
 * **Important:** this matcher works on the [Ingredient] level!
 * Ingredients may contain multiple items, which this matcher must take into account!
 * So iterate over the [Ingredient.choices] and check each entry individually.
 *
 * This is part of a [Registry][com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.ingredientMatchers], so third-parties can add their own custom matchers.
 */
@JsonTypeIdResolver(RegistryKeyTypeIdResolver::class)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = ["type"])
interface IngredientMatcher {

    /**
     * Checks if the source stack matches the given ingredient.
     *
     * @param ingredient the ingredient to check against.
     * @param source the source stack to check.
     *
     * @return the first matching [ItemStackRef], or null if no stack matches.
     */
    fun match(ingredient: Ingredient, source: ItemStackLike): ItemStackRef?

    /**
     * Checks if the item type and the components match.
     */
    interface Exact : IngredientMatcher {

        companion object {

            /**
             * Creates a new instance of the exact [IngredientMatcher].
             *
             * @return a new [Exact] [IngredientMatcher] instance.
             */
            fun of(): Exact = IngredientMatcherExactImpl()

        }

    }

    /**
     * Checks if the item type matches.
     * Additionally, checks if the item contains the specified components [mustContain].
     */
    interface Item : IngredientMatcher {

        companion object {

            /**
             * Creates a new instance of the item [IngredientMatcher].
             *
             * @param mustContain a set of keys that the item must contain in its components.
             * @param mustNotContain a set of keys that the item must not contain in its components.
             *
             * @return a new [Item] [IngredientMatcher] instance.
             */
            fun of(
                mustContain: Set<Key>,
                mustNotContain: Set<Key>,
            ) : Item = IngredientMatcherItemImpl(mustContain, mustNotContain)

        }

        /**
         * The components the stack must contain to pass the check.
         *
         * Default: No components (empty list).
         */
        val mustContain: Set<Key>

        /**
         * The components the stack must **not** contain to pass the check.
         *
         * Default: No components (empty list).
         */
        val mustNotContain: Set<Key>

    }

}
