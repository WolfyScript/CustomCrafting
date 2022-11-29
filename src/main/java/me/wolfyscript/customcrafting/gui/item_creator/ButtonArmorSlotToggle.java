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

package me.wolfyscript.customcrafting.gui.item_creator;

import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import java.util.Locale;
import me.wolfyscript.customcrafting.data.CCCache;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;

public class ButtonArmorSlotToggle {

    private static String key(EquipmentSlot slot) {
        return "armor_slots." + slot.toString().toLowerCase(Locale.ROOT);
    }

    public static void register(GuiMenuComponent.ButtonBuilder<CCCache> builder, EquipmentSlot slot, Material material) {
        builder.toggle(key(slot)).stateFunction((cache, guiHandler, player, guiInventory, i) -> {
            CustomItem item = cache.getItems().getItem();
            return !ItemUtils.isAirOrNull(item) && item.hasEquipmentSlot(slot);
        }).enabledState(state -> state.subKey("enabled").icon(new ItemBuilder(material).addEnchantment(Enchantment.DURABILITY, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS).create())
                .action((cache, guiHandler, player, guiInventory, button, i, event) -> {
                    cache.getItems().getItem().removeEquipmentSlots(slot);
                    return true;
                })
        ).disabledState(state -> state.subKey("disabled").icon(material).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            cache.getItems().getItem().addEquipmentSlots(slot);
            return true;
        })).register();
    }

}
