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

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.tuple.Pair;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabFuel extends ItemCreatorTab {

    public static final String KEY = "fuel";

    public TabFuel() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        var bB = creator.getButtonBuilder();
        ButtonOption.register(bB, Material.COAL, this);
        bB.chatInput("fuel.burn_time.set").state(state -> state.icon(Material.GREEN_CONCRETE).render((cache, guiHandler, player, inventory, btn, itemStack, slot) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("var", String.valueOf(guiHandler.getCustomCache().getItems().getItem().getFuelSettings().getBurnTime()))))).inputAction((guiHandler, player, s, strings) -> {
            try {
                int value = Integer.parseInt(s);
                guiHandler.getCustomCache().getItems().getItem().setBurnTime(value);
                creator.sendMessage(player, "fuel.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                creator.sendMessage(player, "fuel.invalid_value", new Pair<>("%VALUE%", s));
                return true;
            }
            return false;
        }).register();
        bB.action("fuel.burn_time.reset").state(state -> state.icon(Material.RED_CONCRETE).action((cache, guiHandler, player, inventory, btn, i, event) -> {
            var items = cache.getItems();
            items.getItem().setBurnTime(0);
            return true;
        })).register();
        registerFuelToggle(bB, "furnace", Material.FURNACE);
        registerFuelToggle(bB, "blast_furnace", Material.BLAST_FURNACE);
        registerFuelToggle(bB, "smoker", Material.SMOKER);
    }

    private void registerFuelToggle(GuiMenuComponent.ButtonBuilder<CCCache> bB, String id, Material material) {
        bB.toggle("fuel." + id).stateFunction((cache, guiHandler, player, guiInventory, i) -> cache.getItems().getItem().getFuelSettings().getAllowedBlocks().contains(material)).enabledState(state -> state.subKey("enabled").icon(material).action((ItemsButtonAction) (testCache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().getFuelSettings().getAllowedBlocks().remove(material);
            return true;
        })).disabledState(state -> state.subKey("disabled").icon(material).action((ItemsButtonAction) (testCache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().getFuelSettings().getAllowedBlocks().add(material);
            return true;
        })).register();
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
