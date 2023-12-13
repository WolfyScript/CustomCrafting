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
import org.bukkit.inventory.meta.Repairable;

public class TabRepairCost extends ItemCreatorTabVanilla {

    public static final String KEY = "repair_cost";

    public TabRepairCost() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.EXPERIENCE_BOTTLE, this));
        creator.registerButton(new ChatInputButton<>("repair_cost.set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
            BukkitStackIdentifier identifier = guiHandler.getCustomCache().getItems().asBukkitIdentifier().orElse(null);
            if (identifier != null) {
                var itemMeta = identifier.stack().getItemMeta();
                try {
                    int value = Integer.parseInt(s);
                    ((Repairable) itemMeta).setRepairCost(value);
                    identifier.stack().setItemMeta(itemMeta);
                    creator.sendMessage(player, "repair.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
                } catch (NumberFormatException e) {
                    creator.sendMessage(player, "repair.invalid_value", new Pair<>("%VALUE%", s));
                    return true;
                }
            }
            return false;
        }));
        creator.registerButton(new ActionButton<>("repair_cost.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.asBukkitIdentifier().ifPresent(identifier -> {
                var itemMeta = identifier.stack().getItemMeta();
                if (itemMeta instanceof Repairable) {
                    ((Repairable) itemMeta).setRepairCost(0);
                }
                identifier.stack().setItemMeta(itemMeta);
            });
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "repair_cost.set");
        update.setButton(32, "repair_cost.reset");
    }
}
