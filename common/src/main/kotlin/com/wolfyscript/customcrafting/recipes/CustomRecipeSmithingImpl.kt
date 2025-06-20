package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.utils.unwrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

class CustomRecipeSmithingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val template: Ingredient?,
    override val base: Ingredient?,
    override val addition: Ingredient?,
    override val copyOptions: CustomRecipeSmithing.CopyOptions?,
    override val result: RecipeResult,
) : CustomRecipeSmithing {

    override fun evaluate(
        input: RecipeInput.SmithingRecipeInput,
        context: EvaluationContext
    ): RecipeData<CustomRecipeSmithing>? {
        if (!conditions.areSatisfied(context)) {
            return null
        }

        if (template == null && input.template != null || template != null && input.template == null) {
            return null
        }
        val matchedTemplate = template?.let {
            it.match(input.template!!, true)?.let { templateMatch ->
                IngredientDataImpl(0, 0, template, templateMatch)
            } ?: return null
        }

        if (base == null && input.base != null || base != null && input.base == null) {
            return null
        }
        val matchedBase = base?.let {
            it.match(input.base!!, true)?.let { baseMatch ->
                IngredientDataImpl(1, 1, base, baseMatch)
            } ?: return null
        }

        if (addition == null && input.addition != null || addition != null && input.addition == null) {
            return null
        }
        val matchedAddition = addition?.let {
            it.match(input.addition!!, true)?.let { additionMatch ->
                IngredientDataImpl(2, 2, addition, additionMatch)
            } ?: return null
        }

        return RecipeDataImpl(this, arrayOf(matchedTemplate, matchedBase, matchedAddition))
    }

    data class CopyOptionsImpl(override val preserveComponents: List<Key>,
                               override val excludeComponents: List<Key>
    ) : CustomRecipeSmithing.CopyOptions

}

class SmithingUtils {

    companion object {

        fun copyDataComponentsTo(source: ItemStack, dest: ItemStack, options: CustomRecipeSmithing.CopyOptions?) {
            val sourceStack = source.unwrap()
            val destStack = dest.unwrap()

            if (options == null) {
                destStack.applyComponents(sourceStack.componentsPatch)
                return
            }

            val registry = BuiltInRegistries.DATA_COMPONENT_TYPE
            if (options.excludeComponents.isNotEmpty()) {
                // Only include components that are not listed in the exclude list
                for (component in sourceStack.components) {
                    val typeKey = registry.getKey(component.type)
                    if (typeKey != null) {
                        val key = Key.key(typeKey.namespace, typeKey.path)
                        if (options.excludeComponents.contains(key)) {
                            continue
                        }
                    }
                    copyDataComponent(sourceStack, destStack, component.type)
                }
            } else if(options.preserveComponents.isNotEmpty()) {
                // Include all the components listed in the list
                for (key in options.preserveComponents) {
                    registry.get(ResourceLocation.fromNamespaceAndPath(key.namespace, key.value)).ifPresent {
                        copyDataComponent(sourceStack, destStack, it.value())
                    }
                }
            }
        }

        fun copyDataComponentsTo(source: ItemStack, dest: ItemStack, components: List<Key>) {
            val sourceStack = source.unwrap()
            val destStack = dest.unwrap()

            for (key in components) {
                BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.fromNamespaceAndPath(key.namespace, key.value)).ifPresent {
                    copyDataComponent(sourceStack, destStack, it.value())
                }
            }
        }

        private fun <T> copyDataComponent(source: net.minecraft.world.item.ItemStack, dest: net.minecraft.world.item.ItemStack, type: DataComponentType<T>) {
            val value = source.get(type)
            if (value == null) {
                return
            }
            dest.set(type, value)
        }

    }

}