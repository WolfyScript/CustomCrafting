package com.wolfyscript.customcrafting.recipes

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

        return RecipeDataImpl(this, result, arrayOf(matchedTemplate, matchedBase, matchedAddition))
    }

    data class CopyOptionsImpl(override val preserveComponents: List<Key>) : CustomRecipeSmithing.CopyOptions

}

class SmithingUtils {

    companion object {

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