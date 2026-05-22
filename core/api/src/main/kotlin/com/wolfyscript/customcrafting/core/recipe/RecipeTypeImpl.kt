package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.scafall.wrappers.minecraft.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

internal class RecipeTypeImpl<T: CustomRecipe<*,*>>(
    override val recipeClass: Class<T>,
    icon: Item
) : RecipeType<T> {

    override val icon: ItemStackSnapshot = ItemStack(icon).snapshot()

    override fun isInstance(recipe: CustomRecipe<*, *>): Boolean {
        return recipeClass.isInstance(recipe)
    }
}