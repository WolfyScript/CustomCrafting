/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import com.wolfyscript.utilities.bukkit.world.items.reference.BukkitStackIdentifier;
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

public class TabDamage extends ItemCreatorTab {

    public static final String KEY = "damage";

    public TabDamage() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.IRON_SWORD, this));
        creator.registerButton(new ChatInputButton<>("damage.set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
            BukkitStackIdentifier identifier = guiHandler.getCustomCache().getItems().asBukkitIdentifier().orElse(null);
            if (identifier != null) {
                if (!(identifier.stack().getItemMeta() instanceof Damageable)) {
                    return true;
                }
                try {
                    int value = Integer.parseInt(s);
                    guiHandler.getCustomCache().getItems().modifyOriginalStack(stack -> {
                        var itemMeta = stack.getItemMeta();
                        ((Damageable) itemMeta).setDamage(value);
                        stack.setItemMeta(itemMeta);
                    });
                    creator.sendMessage(player, "damage.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
                } catch (NumberFormatException e) {
                    creator.sendMessage(player, "damage.invalid_value", new Pair<>("%VALUE%", s));
                    return true;
                }
            }
            return false;
        }));
        creator.registerButton(new ActionButton<>("damage.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.modifyOriginalStack(stack -> {
                var itemMeta = stack.getItemMeta();
                if (itemMeta instanceof Damageable) {
                    ((Damageable) itemMeta).setDamage(0);
                }
                stack.setItemMeta(itemMeta);
            });
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "damage.set");
        update.setButton(32, "damage.reset");
    }
}
