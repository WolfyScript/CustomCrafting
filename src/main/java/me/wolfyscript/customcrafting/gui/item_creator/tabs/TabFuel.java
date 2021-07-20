package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.ItemCreator;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.FurnaceFuelToggleButton;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.OptionButton;
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

public class TabFuel extends ItemCreatorTab {

    public static final String KEY = "fuel";

    public TabFuel() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(ItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new OptionButton(Material.COAL, this));
        creator.registerButton(new ChatInputButton<>("fuel.burn_time.set", Material.GREEN_CONCRETE, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getBurnTime());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                int value = Integer.parseInt(s);
                guiHandler.getCustomCache().getItems().getItem().setBurnTime(value);
                creator.sendMessage(player, "fuel.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                creator.sendMessage(player, "fuel.invalid_value", new Pair<>("%VALUE%", s));
                return true;
            }
            return false;
        }));
        creator.registerButton(new ActionButton<>("fuel.burn_time.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().setBurnTime(0);
            return true;
        }));
        creator.registerButton(new FurnaceFuelToggleButton("furnace", Material.FURNACE));
        creator.registerButton(new FurnaceFuelToggleButton("blast_furnace", Material.BLAST_FURNACE));
        creator.registerButton(new FurnaceFuelToggleButton("smoker", Material.SMOKER));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "fuel.burn_time.set");
        update.setButton(32, "fuel.burn_time.reset");
        update.setButton(38, "fuel.furnace");
        update.setButton(40, "fuel.blast_furnace");
        update.setButton(42, "fuel.smoker");
    }
}
