package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.inventory.ItemStack;

/**
 * Contains the data for a single Ingredient.
 * <p>
 * It contains:<br>
 * - the recipe slot this data belongs to,<br>
 * - the Ingredient of the recipe,<br>
 * - the CustomItem that was chosen from that Ingredient,<br>
 * - and the created ItemStack of the CustomItem.
 */
public record IngredientData(int recipeSlot, Ingredient ingredient, CustomItem customItem, ItemStack itemStack) {
}
