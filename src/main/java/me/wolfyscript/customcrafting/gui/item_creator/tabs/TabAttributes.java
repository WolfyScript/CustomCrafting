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
import me.wolfyscript.customcrafting.gui.item_creator.ButtonAttributeCategory;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonAttributeMode;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonAttributeSlot;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.UUID;

public class TabAttributes extends ItemCreatorTabVanilla {

    public static final String KEY = "attribute";

    private static final String MAX_HEALTH = "generic_max_health";
    private static final String FOLLOW_RANGE = "generic_follow_range";
    private static final String KNOCKBACK_RESISTANCE = "generic_knockback_resistance";
    private static final String MOVEMENT_SPEED = "generic_movement_speed";
    private static final String FLYING_SPEED = "generic_flying_speed";
    private static final String ATTACK_DAMAGE = "generic_attack_damage";
    private static final String ATTACK_SPEED = "generic_attack_speed";
    private static final String ARMOR = "generic_armor";
    private static final String ARMOR_TOUGHNESS = "generic_armor_toughness";
    private static final String LUCK = "generic_luck";
    private static final String HORSE_JUMB_STRENGTH = "horse_jump_strength";
    private static final String ZOMBIE_SPAWN_REINFORCEMENTS = "zombie_spawn_reinforcements";

    public TabAttributes() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.ENCHANTED_GOLDEN_APPLE, this));
        creator.registerButton(new ButtonAttributeCategory(MAX_HEALTH, Material.ENCHANTED_GOLDEN_APPLE));
        creator.registerButton(new ButtonAttributeCategory(FOLLOW_RANGE, Material.ENDER_EYE));
        creator.registerButton(new ButtonAttributeCategory(KNOCKBACK_RESISTANCE, Material.STICK));
        creator.registerButton(new ButtonAttributeCategory(MOVEMENT_SPEED, Material.IRON_BOOTS));
        creator.registerButton(new ButtonAttributeCategory(FLYING_SPEED, Material.FIREWORK_ROCKET));
        creator.registerButton(new ButtonAttributeCategory(ATTACK_DAMAGE, Material.DIAMOND_SWORD));
        creator.registerButton(new ButtonAttributeCategory(ATTACK_SPEED, Material.DIAMOND_AXE));
        creator.registerButton(new ButtonAttributeCategory(ARMOR, Material.CHAINMAIL_CHESTPLATE));
        creator.registerButton(new ButtonAttributeCategory(ARMOR_TOUGHNESS, Material.DIAMOND_CHESTPLATE));
        creator.registerButton(new ButtonAttributeCategory(LUCK, Material.NETHER_STAR));
        creator.registerButton(new ButtonAttributeCategory(HORSE_JUMB_STRENGTH, Material.DIAMOND_HORSE_ARMOR));
        creator.registerButton(new ButtonAttributeCategory(ZOMBIE_SPAWN_REINFORCEMENTS, Material.ZOMBIE_HEAD));

        creator.registerButton(new ButtonAttributeMode(AttributeModifier.Operation.ADD_NUMBER, "60b55f74681c68283a1c1ce51f1c83b52e2971c91ee34efcb598df3990a7e7"));
        creator.registerButton(new ButtonAttributeMode(AttributeModifier.Operation.ADD_SCALAR, "57b1791bdc46d8a5c51729e8982fd439bb40513f64b5babee93294efc1c7"));
        creator.registerButton(new ButtonAttributeMode(AttributeModifier.Operation.MULTIPLY_SCALAR_1, "a9f27d54ec5552c2ed8f8e1917e8a21cb98814cbb4bc3643c2f561f9e1e69f"));

        creator.registerButton(new ButtonAttributeSlot(EquipmentSlot.HAND, Material.IRON_SWORD));
        creator.registerButton(new ButtonAttributeSlot(EquipmentSlot.OFF_HAND, Material.SHIELD));
        creator.registerButton(new ButtonAttributeSlot(EquipmentSlot.FEET, Material.IRON_BOOTS));
        creator.registerButton(new ButtonAttributeSlot(EquipmentSlot.LEGS, Material.IRON_LEGGINGS));
        creator.registerButton(new ButtonAttributeSlot(EquipmentSlot.CHEST, Material.IRON_CHESTPLATE));
        creator.registerButton(new ButtonAttributeSlot(EquipmentSlot.HEAD, Material.IRON_HELMET));
        creator.registerButton(new ChatInputButton<>("attribute.set_amount", PlayerHeadUtils.getViaURL("461c8febcac21b9f63d87f9fd933589fe6468e93aa81cfcf5e52a4322e16e6"), (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%NUMBER%", guiHandler.getCustomCache().getItems().getAttribAmount());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            try {
                guiHandler.getCustomCache().getItems().setAttribAmount(Double.parseDouble(args[0]));
            } catch (NumberFormatException e) {
                creator.sendMessage(guiHandler, creator.translatedMsgKey("attribute.amount.error"));
                return true;
            }
            return false;
        }));
        creator.registerButton(new ChatInputButton<>("attribute.set_name", Material.NAME_TAG, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%NAME%", guiHandler.getCustomCache().getItems().getAttributeName());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getItems().setAttributeName(strings[0]);
            return false;
        }));
        creator.registerButton(new ChatInputButton<>("attribute.set_uuid", Material.TRIPWIRE_HOOK, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%UUID%", guiHandler.getCustomCache().getItems().getAttributeUUID());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                var uuid = UUID.fromString(strings[0]);
                guiHandler.getCustomCache().getItems().setAttributeUUID(uuid.toString());
            } catch (IllegalArgumentException ex) {
                creator.sendMessage(guiHandler, creator.translatedMsgKey("attribute.uuid.error.line1", Placeholder.unparsed("uuid", strings[0])));
                creator.sendMessage(guiHandler, creator.translatedMsgKey("attribute.uuid.error.line2"));
                return true;
            }
            return false;
        }));
        creator.registerButton(new ActionButton<>("attribute.save", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            var itemMeta = items.getItem().getItemMeta();
            itemMeta.addAttributeModifier(Attribute.valueOf(cache.getSubSetting().split("\\.")[1].toUpperCase(Locale.ROOT)), items.getAttributeModifier());
            items.getItem().setItemMeta(itemMeta);
            return true;
        }));
        creator.registerButton(new ActionButton<>("attribute.delete", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, i, event) -> {
            ChatUtils.sendAttributeModifierManager(player);
            guiHandler.close();
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        if (cache.getSubSetting().startsWith("attribute.generic") || cache.getSubSetting().startsWith("attribute.horse") || cache.getSubSetting().startsWith("attribute.zombie")) {
            update.setButton(27, "attribute.slot_head");
            update.setButton(36, "attribute.slot_chest");
            update.setButton(28, "attribute.slot_legs");
            update.setButton(37, "attribute.slot_feet");
            update.setButton(29, "attribute.slot_hand");
            update.setButton(38, "attribute.slot_off_hand");
            update.setButton(33, "attribute.multiply_scalar_1");
            update.setButton(34, "attribute.add_scalar");
            update.setButton(35, "attribute.add_number");
            update.setButton(42, "attribute.set_amount");
            update.setButton(43, "attribute.set_name");
            update.setButton(44, "attribute.set_uuid");
            update.setButton(31, "attribute.save");
            update.setButton(40, "attribute.delete");
            return;
        }
        setSectionButton(update, 27, MAX_HEALTH);
        setSectionButton(update, 28, FOLLOW_RANGE);
        setSectionButton(update, 29, KNOCKBACK_RESISTANCE);
        setSectionButton(update, 30, MOVEMENT_SPEED);
        setSectionButton(update, 31, FLYING_SPEED);
        setSectionButton(update, 32, ATTACK_DAMAGE);
        setSectionButton(update, 33, ATTACK_SPEED);
        setSectionButton(update, 34, ARMOR);
        setSectionButton(update, 35, ARMOR_TOUGHNESS);
        setSectionButton(update, 39, LUCK);
        setSectionButton(update, 40, HORSE_JUMB_STRENGTH);
        setSectionButton(update, 41, ZOMBIE_SPAWN_REINFORCEMENTS);
    }

    private void setSectionButton(GuiUpdate<CCCache> update, int slot, String section) {
        update.setButton(slot, KEY + "." + section);
    }
}
