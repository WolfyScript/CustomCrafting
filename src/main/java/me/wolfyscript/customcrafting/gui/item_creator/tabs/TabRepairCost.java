package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.OptionButton;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

public class TabRepairCost extends ItemCreatorTab {

    public static final String KEY = "repair_cost";

    public TabRepairCost() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register() {
        creator.registerButton(new OptionButton(Material.EXPERIENCE_BOTTLE, this));
        creator.registerButton(new ChatInputButton<>("repair_cost.set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            try {
                int value = Integer.parseInt(s);
                ((Repairable) itemMeta).setRepairCost(value);
                guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                creator.sendMessage(player, "repair.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                creator.sendMessage(player, "repair.invalid_value", new Pair<>("%VALUE%", s));
                return true;
            }
            return false;
        }));
        creator.registerButton(new ActionButton<>("repair_cost.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            var itemMeta = items.getItem().getItemMeta();
            if (itemMeta instanceof Repairable) {
                ((Repairable) itemMeta).setRepairCost(0);
            }
            items.getItem().setItemMeta(itemMeta);
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "repair_cost.set");
        update.setButton(32, "repair_cost.reset");
        update.setButton(36, "meta_ignore.wolfyutilities.repairCost");
    }
}
