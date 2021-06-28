package me.wolfyscript.customcrafting.utils.recipe_item.target.adapters;

import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.recipe_item.target.MergeAdapter;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantMergeAdapter extends MergeAdapter {

    private final boolean ignoreEnchantLimit = false;
    private List<Enchantment> blackListedEnchants = new ArrayList<>();

    public EnchantMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "enchant"));
    }

    public EnchantMergeAdapter(EnchantMergeAdapter adapter) {
        super(adapter);
    }

    @Override
    public ItemStack mergeCrafting(CraftingData craftingData, Player player, CustomItem customResult, ItemStack result) {
        for (IngredientData data : craftingData.getBySlots(slots)) {
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

    public boolean isIgnoreEnchantLimit() {
        return ignoreEnchantLimit;
    }

    @JsonProperty("blackListedEnchants")
    public List<String> getBlackListedEnchants() {
        return blackListedEnchants.stream().map(enchantment -> enchantment.getKey().toString()).collect(Collectors.toList());
    }

    @JsonProperty("blackListedEnchants")
    public void setBlackListedEnchants(List<String> blackListedEnchants) {
        this.blackListedEnchants = blackListedEnchants.stream().map(s -> Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(s))).collect(Collectors.toList());
    }

    @Override
    public ItemStack merge(ItemStack[] ingredients, @Nullable Player player, CustomItem customResult, ItemStack result) {
        return null;
    }

    @Override
    public MergeAdapter clone() {
        return new EnchantMergeAdapter(this);
    }
}
