package me.wolfyscript.customcrafting.recipes.recipe_item.target.adapters;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.recipe_item.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This MergeAdapter shows another functionality it can be used for.
 * <p>
 * Using this adapter you can replace placeholders of the resulting item before it's even taken out of the inventory.
 * <br>
 * Providing a preview for the player to tell what he will actually craft.
 */
public class PlaceholderAPIMergeAdapter extends MergeAdapter {

    private boolean replaceName = true;
    private boolean nameBracketPlaceholders = true;
    private boolean replaceLore = true;
    private boolean loreBracketPlaceholders = true;

    public PlaceholderAPIMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "placeholderapi"));
    }

    public PlaceholderAPIMergeAdapter(PlaceholderAPIMergeAdapter adapter) {
        super(adapter);
        this.replaceName = adapter.replaceName;
        this.replaceLore = adapter.replaceLore;
        this.nameBracketPlaceholders = adapter.nameBracketPlaceholders;
        this.loreBracketPlaceholders = adapter.loreBracketPlaceholders;
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        if (player != null && WolfyUtilities.hasPlaceHolderAPI() && result.hasItemMeta()) {
            var meta = result.getItemMeta();
            if (replaceName) {
                String name = meta.getDisplayName();
                if (PlaceholderAPI.containsPlaceholders(name)) {
                    name = PlaceholderAPI.setPlaceholders(player, name);
                }
                if (nameBracketPlaceholders && PlaceholderAPI.containsBracketPlaceholders(name)) {
                    name = PlaceholderAPI.setBracketPlaceholders(player, name);
                }
                meta.setDisplayName(ChatColor.convert(name));
            }
            if (replaceLore && meta.hasLore()) {
                List<String> lore = meta.getLore();
                lore = PlaceholderAPI.setPlaceholders(player, lore);
                if (loreBracketPlaceholders) {
                    lore = PlaceholderAPI.setBracketPlaceholders(player, lore);
                }
                meta.setLore(lore);
            }
            result.setItemMeta(meta);
        }
        return result;
    }

    public boolean isReplaceLore() {
        return replaceLore;
    }

    public boolean isReplaceName() {
        return replaceName;
    }

    @Override
    public MergeAdapter clone() {
        return new PlaceholderAPIMergeAdapter(this);
    }
}
