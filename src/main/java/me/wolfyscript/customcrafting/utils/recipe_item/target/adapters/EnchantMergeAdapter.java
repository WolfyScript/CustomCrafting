package me.wolfyscript.customcrafting.utils.recipe_item.target.adapters;

import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.recipe_item.target.MergeAdapter;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EnchantMergeAdapter extends MergeAdapter {

    public EnchantMergeAdapter() {
        super(NamespacedKey.wolfyutilties("enchant"));
    }

    @Override
    public ItemStack mergeCrafting(CraftingData craftingData, Player player, CustomItem customResult, ItemStack result) {
        for (IngredientData data : craftingData.getBySlots(slots)) {
            var item = data.itemStack();
            item.getEnchantments().forEach((enchantment, level) -> {
                if (result.containsEnchantment(enchantment) && result.getEnchantmentLevel(enchantment) < level) {
                    result.addUnsafeEnchantment(enchantment, level);
                }
            });
        }
        return result;
    }

    @Override
    public ItemStack merge(ItemStack[] ingredients, @Nullable Player player, CustomItem customResult, ItemStack result) {
        return null;
    }
}
