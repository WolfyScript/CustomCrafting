package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.ItemCreator;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.OptionButton;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabCustomDurability extends ItemCreatorTab {

    public static final String KEY = "custom_durability";

    public TabCustomDurability() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(ItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new OptionButton(Material.DIAMOND_SWORD, this));
        creator.registerButton(new ActionButton<>("custom_durability.remove", Material.RED_CONCRETE_POWDER, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().removeCustomDurability();
            return true;
        }));
        creator.registerButton(new ChatInputButton<>("custom_durability.set_durability", Material.GREEN_CONCRETE, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getCustomDurability());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                guiHandler.getCustomCache().getItems().getItem().setCustomDurability(Integer.parseInt(strings[0]));
            } catch (NumberFormatException ex) {
                return true;
            }
            guiHandler.openCluster();
            return false;
        }));
        creator.registerButton(new ChatInputButton<>("custom_durability.set_damage", Material.RED_CONCRETE, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var items = guiHandler.getCustomCache().getItems();
            values.put("%VAR%", items.getItem().getCustomDamage());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                guiHandler.getCustomCache().getItems().getItem().setCustomDamage(Integer.parseInt(strings[0]));
            } catch (NumberFormatException ex) {
                return true;
            }
            guiHandler.openCluster();
            return false;
        }));
        creator.registerButton(new ChatInputButton<>("custom_durability.set_tag", Material.NAME_TAG, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var items = guiHandler.getCustomCache().getItems();
            values.put("%VAR%", items.getItem().getCustomDurabilityTag());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                guiHandler.getCustomCache().getItems().getItem().setCustomDurabilityTag("&r" + s);
            } catch (NumberFormatException ex) {
                return true;
            }
            guiHandler.openCluster();
            return false;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(29, "custom_durability.set_damage");
        update.setButton(31, "custom_durability.set_tag");
        update.setButton(33, "custom_durability.set_durability");
        update.setButton(40, "custom_durability.remove");
        update.setButton(36, "meta_ignore.wolfyutilities.custom_damage");
        update.setButton(44, "meta_ignore.wolfyutilities.custom_durability");
    }
}
