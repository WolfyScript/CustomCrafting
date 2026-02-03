package com.wolfyscript.customcrafting.core.util

import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.core.recipes.RecipeResult
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.unwrap
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.display.SlotDisplay

object IngredientUtils {

    fun toMcIngredient(ingredient: Ingredient) : net.minecraft.world.item.crafting.Ingredient {
        return ingredient.toMc()
    }

}

fun Ingredient.toMc() : net.minecraft.world.item.crafting.Ingredient {
    return net.minecraft.world.item.crafting.Ingredient.of(HolderSet.direct(choices.stacks.map { stack -> BuiltInRegistries.ITEM.wrapAsHolder(stack.create().unwrap().item) }))
}

fun Ingredient?.toMcDisplay() : SlotDisplay {
    if (this == null) {
        return SlotDisplay.Empty.INSTANCE
    }
    val stacks = choices.all()
    return SlotDisplay.Composite(stacks.map { it.toMcDisplay(matching is IngredientMatcher.Exact) })
}

fun ItemStackRef.toMcDisplay(itemOnly: Boolean = false) : SlotDisplay {
    val mcStack = this.create().unwrap()
    if (itemOnly) {
        return SlotDisplay.ItemSlotDisplay(mcStack.item)
    }
    return SlotDisplay.ItemStackSlotDisplay(mcStack)
}

fun RecipeResult.toMcDisplay() : SlotDisplay {
    val stacks = choices.all()
    return SlotDisplay.Composite(stacks.map { it.toMcDisplay() })
}