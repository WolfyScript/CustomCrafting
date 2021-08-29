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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabRarity extends ItemCreatorTab {

    public static final String KEY = "rarity";

    public TabRarity() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.DIAMOND, this));
        creator.registerButton(new ChatInputButton<>("rarity.set", Material.GREEN_CONCRETE, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getRarityPercentage() + "§8(§7" + (cache.getItems().getItem().getRarityPercentage() * 100) + "%§8)");
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                guiHandler.getCustomCache().getItems().getItem().setRarityPercentage(Double.parseDouble(s));
            } catch (NumberFormatException ex) {
                return true;
            }
            return false;
        }));
        creator.registerButton(new ActionButton<>("rarity.reset", Material.RED_CONCRETE_POWDER, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().setRarityPercentage(1.0d);
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "rarity.set");
        update.setButton(32, "rarity.reset");
    }
}
