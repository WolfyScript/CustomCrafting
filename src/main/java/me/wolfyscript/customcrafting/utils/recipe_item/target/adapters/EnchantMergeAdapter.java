package me.wolfyscript.customcrafting.utils.recipe_item.target.adapters;

import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.recipe_item.target.MergeAdapter;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnchantMergeAdapter extends MergeAdapter {

    private final boolean ignoreEnchantLimit = false;
    private List<Enchantment> blackListedEnchants = new ArrayList<>();

    public EnchantMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "enchant"));
    }

    public EnchantMergeAdapter(EnchantMergeAdapter adapter) {
        super(adapter);
    }

    public boolean isIgnoreEnchantLimit() {
        return ignoreEnchantLimit;
    }

    @JsonProperty("blackListedEnchants")
    public List<String> getBlackListedEnchants() {
        return blackListedEnchants.stream().map(enchantment -> enchantment.getKey().toString()).toList();
    }

    @JsonProperty("blackListedEnchants")
    public void setBlackListedEnchants(List<String> blackListedEnchants) {
        this.blackListedEnchants = blackListedEnchants.stream().map(s -> Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(s))).toList();
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        for (IngredientData data : recipeData.getBySlots(slots)) {
            var item = data.itemStack();
            item.getEnchantments().forEach((enchantment, level) -> {
                if (!blackListedEnchants.contains(enchantment) && !result.containsEnchantment(enchantment) && result.getEnchantmentLevel(enchantment) < level) {
                    var meta = result.getItemMeta();
                    if (meta != null && meta.addEnchant(enchantment, level, ignoreEnchantLimit)) {
                        result.setItemMeta(meta);
                    }
                }
            });
        }
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new EnchantMergeAdapter(this);
    }
}
