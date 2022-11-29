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

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonFurnaceFuelToggle;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabFuel extends ItemCreatorTab {

    public static final String KEY = "fuel";

    public TabFuel() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        ButtonOption.register(creator.getButtonBuilder(), Material.COAL, this);
        creator.registerButton(new ButtonChatInput<>("fuel.burn_time.set", Material.GREEN_CONCRETE, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
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
        creator.registerButton(new ButtonAction<>("fuel.burn_time.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().setBurnTime(0);
            return true;
        }));
        creator.registerButton(new ButtonFurnaceFuelToggle("furnace", Material.FURNACE));
        creator.registerButton(new ButtonFurnaceFuelToggle("blast_furnace", Material.BLAST_FURNACE));
        creator.registerButton(new ButtonFurnaceFuelToggle("smoker", Material.SMOKER));
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
