package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabUnbreakable extends ItemCreatorTab {

    public static final String KEY = "unbreakable";

    public TabUnbreakable() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register() {
        creator.registerButton(new ToggleButton<>(KEY, (cache, guiHandler, player, guiInventory, i) -> {
            CustomItem item = cache.getItems().getItem();
            return !ItemUtils.isAirOrNull(item) && item.getItemMeta().isUnbreakable();
        }, new ButtonState<>(KEY + ".enabled", Material.BEDROCK, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            var itemMeta = items.getItem().getItemMeta();
            itemMeta.setUnbreakable(false);
            items.getItem().setItemMeta(itemMeta);
            return true;
        }), new ButtonState<>(KEY + ".disabled", Material.GLASS, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            var itemMeta = items.getItem().getItemMeta();
            itemMeta.setUnbreakable(true);
            items.getItem().setItemMeta(itemMeta);
            return true;
        })));
    }

    @Override
    public String getOptionButton() {
        return KEY;
    }

    @Override
    public boolean shouldRender(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        return !ItemUtils.isAirOrNull(item);
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, KEY + ".set");
        update.setButton(32, KEY + ".remove");
        update.setButton(36, "meta_ignore.wolfyutilities.name");
    }
}
