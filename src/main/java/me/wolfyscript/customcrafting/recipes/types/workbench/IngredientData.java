package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.inventory.ItemStack;

public record IngredientData(Ingredient ingredient, CustomItem customItem, ItemStack itemStack) {
}
