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
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonDummy;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonItemInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonToggle;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.tuple.Pair;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabConsume extends ItemCreatorTab {

    public static final String KEY = "consume";

    public TabConsume() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        ButtonOption.register(creator.getButtonBuilder(), Material.ITEM_FRAME, this);
        creator.getButtonBuilder().chatInput(KEY + ".durability_cost.enabled").state(state -> state.icon(Material.DROPPER).render((cache, guiHandler, player, inventory, btn, itemStack, slot) -> {
            return CallbackButtonRender.UpdateResult.of(Placeholder.parsed("var", String.valueOf(guiHandler.getCustomCache().getItems().getItem().getDurabilityCost())));
        })).inputAction((guiHandler, player, s, strings) -> {
            try {
                int value = Integer.parseInt(s);
                guiHandler.getCustomCache().getItems().getItem().setDurabilityCost(value);
                creator.sendMessage(player, "consume.valid", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                creator.sendMessage(player, "consume.invalid", new Pair<>("%VALUE%", s));
                return true;
            }
            return false;
        }).register();
        creator.getButtonBuilder().dummy(KEY + ".durability_cost.disabled").state(state -> state.icon(Material.DROPPER)).register();

        creator.getButtonBuilder().toggle(KEY + ".consume_item").stateFunction((cache, guiHandler, player, guiInventory, i) -> cache.getItems().getItem().isConsumed())
                .enabledState(state -> state.subKey("consume.consume_item.enabled").icon(Material.GREEN_CONCRETE).action((cache, guiHandler, player, inventory, btn, i, event) -> {
            var items = cache.getItems();
            items.getItem().setConsumed(false);
            return true;
        })).disabledState(state -> state.subKey(KEY + ".consume_item.disabled").icon(Material.RED_CONCRETE).action((cache, guiHandler, player, inventory, btn, i, event) -> {
            var items = cache.getItems();
            items.getItem().setConsumed(true);
            return true;
        })).register();

        creator.getButtonBuilder().dummy(KEY + ".replacement.enabled").state(state -> state.icon(Material.GREEN_CONCRETE)).register();
        creator.getButtonBuilder().dummy(KEY + ".replacement.disabled").state(state -> state.icon(Material.RED_CONCRETE)).register();

        creator.getButtonBuilder().itemInput(KEY + ".replacement").state(state -> state.icon(Material.AIR).postAction((cache, guiHandler, player, guiInventory, button, itemStack, slot, inventoryInteractEvent) -> {
            ItemStack replacement = guiInventory.getItem(slot);
            if (replacement != null) {
                cache.getItems().getItem().setReplacement(CustomItem.getReferenceByItemStack(replacement).getApiReference());
            } else {
                cache.getItems().getItem().setReplacement(null);
            }
        }).render((cache, guiHandler, player, guiInventory, button, itemStack, i) -> guiHandler.getCustomCache().getItems().getItem().hasReplacement() ? CallbackButtonRender.UpdateResult.of(CustomItem.with(cache.getItems().getItem().getReplacement()).create()) : CallbackButtonRender.UpdateResult.of(new ItemStack(Material.AIR)))).register();
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(31, "consume.consume_item");
        update.setButton(38, "consume.replacement");
        update.setButton(39, items.getItem().hasReplacement() ? "consume.replacement.enabled" : "consume.replacement.disabled");
        if (customItem.hasReplacement() || item.getMaxStackSize() > 1) {
            update.setButton(41, "consume.durability_cost.disabled");
        } else {
            update.setButton(41, "consume.durability_cost.enabled");
        }
    }
}
