package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.utils.wrap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack

class RecipeChoicesImpl(
    override val stacks: List<ItemStackRef> = emptyList(),
    override val tags: List<Key> = emptyList(),
) : RecipeChoices {

    private val combinedChoices: List<ItemStackRef>

    init {
        val list = mutableListOf<ItemStackRef>()
        list.addAll(stacks)

        for (key in tags) {
            val tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), key.toMc())
            BuiltInRegistries.ITEM.get(tagKey).ifPresent {
                for (holder in it) {
                    list.add(ItemStackRef.create(ItemStack(holder.value()).wrap()))
                }
            }
        }
        combinedChoices = list
    }


    override fun all(): List<ItemStackRef> {
        return combinedChoices
    }

    @Deprecated("Unsure if this is actually needed. Use all() instead for now.")
    override fun allFor(context: EvaluationContext): List<ItemStackRef> {
        return all() // TODO: Filter items, like permission etc. // Do we actually need this?
    }

    override fun toString(): String {
        return "$stacks or $tags"
    }

}