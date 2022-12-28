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
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import java.util.Locale;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class TabArmorSlots extends ItemCreatorTab {

    public static final String KEY = "armor_slots";

    public TabArmorSlots() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        ButtonOption.register(creator.getButtonBuilder(), Material.IRON_HELMET, this);
        register(api, creator.getButtonBuilder(), EquipmentSlot.HEAD, Material.DIAMOND_HELMET);
        register(api, creator.getButtonBuilder(), EquipmentSlot.CHEST, Material.DIAMOND_CHESTPLATE);
        register(api, creator.getButtonBuilder(), EquipmentSlot.LEGS, Material.DIAMOND_LEGGINGS);
        register(api, creator.getButtonBuilder(), EquipmentSlot.FEET, Material.DIAMOND_BOOTS);
    }

    public static void register(WolfyUtilsBukkit api, GuiMenuComponent.ButtonBuilder<CCCache> builder, EquipmentSlot equipmentSlot, Material material) {
        builder.toggle(key(equipmentSlot)).stateFunction((holder, cache, slot) -> {
            CustomItem item = cache.getItems().getItem();
            return !ItemUtils.isAirOrNull(item) && item.hasEquipmentSlot(equipmentSlot);
        }).enabledState(state -> state.subKey("enabled").icon(new ItemBuilder(api, material).addEnchantment(Enchantment.DURABILITY, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS).create())
                .action((holder, cache, btn, slot, details) -> {
                    cache.getItems().getItem().removeEquipmentSlots(equipmentSlot);
                    return ButtonInteractionResult.cancel(true);
                })
        ).disabledState(state -> state.subKey("disabled").icon(material).action((holder, cache, btn, slot, details) -> {
            cache.getItems().getItem().addEquipmentSlots(equipmentSlot);
            return ButtonInteractionResult.cancel(true);
        })).register();
    }

    private static String key(EquipmentSlot slot) {
        return "armor_slots." + slot.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(28, "armor_slots.head");
        update.setButton(30, "armor_slots.chest");
        update.setButton(32, "armor_slots.legs");
        update.setButton(34, "armor_slots.feet");
    }
}
