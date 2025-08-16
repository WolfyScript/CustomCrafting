package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.*
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.toScafall
import com.wolfyscript.scafall.wrappers.utils.unwrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

class CustomRecipeSmithingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val template: Ingredient?,
    override val base: Ingredient,
    override val addition: Ingredient?,
    override val copyOptions: CustomRecipeSmithing.CopyOptions?,
    override val result: RecipeResult,
) : CustomRecipeSmithing {

    override fun evaluate(
        input: RecipeInput.SmithingRecipeInput,
        context: EvaluationContext,
    ): RecipeEvaluationResult.Data? {
        if (!conditions.areSatisfied(context)) {
            return null
        }
        if (
            !validIngredient(template, input.template) ||
            (input.base == null || input.base!!.isEmpty) ||
            !validIngredient(addition, input.addition)
        ) {
            return null
        }

        val matchedTemplate = evaluateIngredient(template, input.template)
        val matchedBase = evaluateIngredient(base, input.base)
        val matchedAddition = evaluateIngredient(addition, input.addition)

        return DefaultDataImpl(arrayOf(matchedTemplate, matchedBase, matchedAddition))
    }

    private fun validIngredient(ingredient: Ingredient?, inputStack: ItemStack?): Boolean {
        val emptyStack = inputStack == null || inputStack.isEmpty
        if (ingredient == null) {
            return emptyStack
        }
        return !emptyStack
    }

    private fun evaluateIngredient(ingredient: Ingredient?, inputStack: ItemStack?): IngredientData? {
        if (ingredient == null || inputStack == null || inputStack.isEmpty) {
            return null
        }
        return ingredient.match(inputStack)?.let { ingredientMatch ->
            IngredientDataImpl(0, 0, ingredient, ingredientMatch)
        }
    }

    override fun toString(): String {
        return "smithing ($priority), template=$template, base=$base, addition=$addition, copying $copyOptions, producing $result if $conditions"
    }

    data class CopyOptionsImpl(
        override val preserveComponents: List<Key> = emptyList(),
        override val excludeComponents: List<Key> = emptyList(),
    ) : CustomRecipeSmithing.CopyOptions {

        override fun toString(): String {
            return "(preserve $preserveComponents, exclude $excludeComponents)"
        }
    }

}

class SmithingUtils {

    companion object {

        fun copyDataComponentsTo(source: ItemStack, dest: ItemStack, options: CustomRecipeSmithing.CopyOptions?) {
            val sourceStack = source.unwrap()
            val destStack = dest.unwrap()

            if (options == null || options.preserveComponents.isEmpty() && options.excludeComponents.isEmpty()) {
                destStack.applyComponents(sourceStack.componentsPatch)
                return
            }

            val registry = BuiltInRegistries.DATA_COMPONENT_TYPE
            if (options.excludeComponents.isNotEmpty()) {
                // Only include components that are not listed in the exclude list
                for (component in sourceStack.components) {
                    val typeKey = registry.getKey(component.type)
                    if (typeKey != null) {
                        val key = typeKey.toScafall()
                        if (options.excludeComponents.contains(key)) {
                            continue
                        }
                    }
                    copyDataComponent(sourceStack, destStack, component.type)
                }
            } else if (options.preserveComponents.isNotEmpty()) {
                // Include all the components listed in the list
                for (key in options.preserveComponents) {
                    registry.get(key.toMc()).ifPresent {
                        copyDataComponent(sourceStack, destStack, it.value())
                    }
                }
            }
        }

        fun copyDataComponentsTo(source: ItemStack, dest: ItemStack, components: List<Key>) {
            val sourceStack = source.unwrap()
            val destStack = dest.unwrap()

            for (key in components) {
                BuiltInRegistries.DATA_COMPONENT_TYPE.get(
                    ResourceLocation.fromNamespaceAndPath(key.namespace, key.value)
                ).ifPresent {
                    copyDataComponent(sourceStack, destStack, it.value())
                }
            }
        }

        private fun <T> copyDataComponent(
            source: net.minecraft.world.item.ItemStack,
            dest: net.minecraft.world.item.ItemStack,
            type: DataComponentType<T>,
        ) {
            val value = source.get(type) ?: return
            dest.set(type, value)
        }

    }

}