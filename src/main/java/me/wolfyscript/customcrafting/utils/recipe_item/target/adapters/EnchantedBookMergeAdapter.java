package me.wolfyscript.customcrafting.utils.recipe_item.target.adapters;

import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.recipe_item.target.MergeAdapter;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.Nullable;

public class EnchantedBookMergeAdapter extends MergeAdapter {

    public EnchantedBookMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "enchanted_book"));
    }

    public EnchantedBookMergeAdapter(EnchantedBookMergeAdapter adapter) {
        super(adapter);
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        for (IngredientData data : recipeData.getBySlots(slots)) {
            if (data.itemStack().getItemMeta() instanceof EnchantmentStorageMeta meta) {
                meta.getStoredEnchants().forEach((enchantment, level) -> {
                    if (!result.containsEnchantment(enchantment) && result.getEnchantmentLevel(enchantment) < level) {
                        result.addUnsafeEnchantment(enchantment, level);
                    }
                });
            }
        }
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new EnchantedBookMergeAdapter(this);
    }
}
