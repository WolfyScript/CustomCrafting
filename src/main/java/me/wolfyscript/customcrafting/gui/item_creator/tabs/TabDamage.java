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
import org.bukkit.inventory.meta.Damageable;

public class TabDamage extends ItemCreatorTabVanilla {

    public static final String KEY = "damage";

    public TabDamage() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.IRON_SWORD, this));
        creator.registerButton(new ChatInputButton<>("damage.set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            if (!(itemMeta instanceof Damageable)) {
                return true;
            }
            try {
                int value = Integer.parseInt(s);
                ((Damageable) itemMeta).setDamage(value);
                guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                creator.sendMessage(player, "damage.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                creator.sendMessage(player, "damage.invalid_value", new Pair<>("%VALUE%", s));
                return true;
            }
            return false;
        }));
        creator.registerButton(new ActionButton<>("damage.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            var itemMeta = items.getItem().getItemMeta();
            if (itemMeta instanceof Damageable) {
                ((Damageable) itemMeta).setDamage(0);
            }
            items.getItem().setItemMeta(itemMeta);
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "damage.set");
        update.setButton(32, "damage.reset");
        update.setButton(36, "meta_ignore.wolfyutilities.damage");
    }
}
