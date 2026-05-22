package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.toScafall
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack

object SmithingUtils {

    fun copyDataComponentsTo(
        source: ScafallItemStack,
        dest: ScafallItemStack,
        options: CustomRecipeSmithing.CopyOptions?,
    ) {
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

    fun copyDataComponentsTo(source: ScafallItemStack, dest: ScafallItemStack, components: List<Key>) {
        val sourceStack = source.unwrap()
        val destStack = dest.unwrap()

        for (key in components) {
            BuiltInRegistries.DATA_COMPONENT_TYPE.get(
                Identifier.fromNamespaceAndPath(key.namespace, key.value)
            ).ifPresent {
                copyDataComponent(sourceStack, destStack, it.value())
            }
        }
    }

    private fun <T : Any> copyDataComponent(
        source: ItemStack,
        dest: ItemStack,
        type: DataComponentType<T>,
    ) {
        val value = source.get(type) ?: return
        dest.set(type, value)
    }

}