package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

public class TabCustomModelData extends ItemCreatorTab {

    public static final String KEY = "custom_model_data";

    public TabCustomModelData() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.REDSTONE, this));
        creator.registerButton(new ChatInputButton<>("custom_model_data.set", Material.GREEN_CONCRETE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var items = guiHandler.getCustomCache().getItems();
            hashMap.put("%VAR%", (items.getItem().hasItemMeta() && items.getItem().getItemMeta().hasCustomModelData() ? items.getItem().getItemMeta().getCustomModelData() : "&7&l/") + "");
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            if (!(itemMeta instanceof Repairable)) {
                return true;
            }
            try {
                int value = Integer.parseInt(s);
                itemMeta.setCustomModelData(value);
                guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                creator.sendMessage(player, "custom_model_data.success", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                creator.sendMessage(player, "custom_model_data.invalid_value", new Pair<>("%VALUE%", s));
                return true;
            }
            return false;
        }));
        creator.registerButton(new ActionButton<>("custom_model_data.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            var itemMeta = items.getItem().getItemMeta();
            itemMeta.setCustomModelData(null);
            items.getItem().setItemMeta(itemMeta);
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "custom_model_data.set");
        update.setButton(32, "custom_model_data.reset");
        update.setButton(36, "meta_ignore.wolfyutilities.customModelData");
    }
}
