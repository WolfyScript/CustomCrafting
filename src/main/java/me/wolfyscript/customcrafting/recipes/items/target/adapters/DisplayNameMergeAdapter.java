package me.wolfyscript.customcrafting.recipes.items.target.adapters;

import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DisplayNameMergeAdapter extends MergeAdapter {

    private boolean appendNames;
    private String extra;

    public DisplayNameMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "display_name"));
        this.appendNames = false;
        this.extra = null;
    }

    public DisplayNameMergeAdapter(DisplayNameMergeAdapter adapter) {
        super(adapter);
        this.appendNames = adapter.appendNames;
        this.extra = adapter.extra;
    }

    public void setAppendNames(boolean appendNames) {
        this.appendNames = appendNames;
    }

    public boolean isAppendNames() {
        return appendNames;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtra() {
        return extra;
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        var resultMeta = result.getItemMeta();
        for (IngredientData data : recipeData.getBySlots(slots)) {
            var item = data.itemStack();
            var meta = item.getItemMeta();
            if (appendNames) {
                resultMeta.setDisplayName(meta.getDisplayName() + resultMeta.getDisplayName());
            } else {
                resultMeta.setDisplayName(meta.getDisplayName());
            }
        }
        if (extra != null) {
            resultMeta.setDisplayName(resultMeta.getDisplayName() + ChatColor.convert(extra));
        }
        result.setItemMeta(resultMeta);
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new DisplayNameMergeAdapter(this);
    }
}
