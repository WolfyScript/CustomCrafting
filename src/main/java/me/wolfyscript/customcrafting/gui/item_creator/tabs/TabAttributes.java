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
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import java.util.Locale;
import java.util.UUID;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

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
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    public static void registerCategory(GuiMenuComponent.ButtonBuilder<CCCache> bB, String attribute, Material material) {
        bB.action("attribute." + attribute).state(state -> state.icon(material).action((holder, cache, btn, slot, details) -> {
            holder.getGuiHandler().getCustomCache().setSubSetting("attribute." + attribute);
            return ButtonInteractionResult.cancel(true);
        })).register();
    }

    public static void registerMode(GuiMenuComponent.ButtonBuilder<CCCache> bB, AttributeModifier.Operation operation, String headURLValue) {
        bB.action("attribute." + operation.toString().toLowerCase(Locale.ROOT)).state(state -> state.icon(PlayerHeadUtils.getViaURL(headURLValue)).action((holder, cache, btn, slot, details) -> {
            holder.getGuiHandler().getCustomCache().getItems().setAttribOperation(operation);
            return ButtonInteractionResult.cancel(true);
        }).render((holder, cache, btn, slot, itemStack) -> CallbackButtonRender.Result.of(Placeholder.parsed("c", holder.getGuiHandler().getCustomCache().getItems().getAttribOperation().equals(operation) ? "<green>" : "<red>")))).register();
    }

    public static void registerAttributeSlot(GuiMenuComponent.ButtonBuilder<CCCache> bB, EquipmentSlot equipmentSlot, Material material) {
        bB.action("attribute.slot_" + equipmentSlot.toString().toLowerCase(Locale.ROOT)).state(state -> state.icon(material)
                .action((holder, cache, btn, slot, details) -> {
                    Items items = holder.getGuiHandler().getCustomCache().getItems();
                    items.setAttributeSlot(items.getAttributeSlot() == null ? equipmentSlot : (items.getAttributeSlot().equals(equipmentSlot) ? null : equipmentSlot));
                    return ButtonInteractionResult.cancel(true);
                }).render((holder, cache, btn, slot, itemStack) -> {
                    if (holder.getGuiHandler().getCustomCache().getItems().isAttributeSlot(equipmentSlot)) {
                        return CallbackButtonRender.Result.of(new ItemBuilder(holder.getWindow().getWolfyUtils(), itemStack).addEnchantment(Enchantment.DURABILITY, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS).create());
                    }
                    return CallbackButtonRender.Result.of();
                })
        ).register();
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        GuiMenuComponent.ButtonBuilder<CCCache> bB = creator.getButtonBuilder();
        ButtonOption.register(creator.getButtonBuilder(), Material.ENCHANTED_GOLDEN_APPLE, this);
        registerCategory(bB, MAX_HEALTH, Material.ENCHANTED_GOLDEN_APPLE);
        registerCategory(bB, FOLLOW_RANGE, Material.ENDER_EYE);
        registerCategory(bB, KNOCKBACK_RESISTANCE, Material.STICK);
        registerCategory(bB, MOVEMENT_SPEED, Material.IRON_BOOTS);
        registerCategory(bB, FLYING_SPEED, Material.FIREWORK_ROCKET);
        registerCategory(bB, ATTACK_DAMAGE, Material.DIAMOND_SWORD);
        registerCategory(bB, ATTACK_SPEED, Material.DIAMOND_AXE);
        registerCategory(bB, ARMOR, Material.CHAINMAIL_CHESTPLATE);
        registerCategory(bB, ARMOR_TOUGHNESS, Material.DIAMOND_CHESTPLATE);
        registerCategory(bB, LUCK, Material.NETHER_STAR);
        registerCategory(bB, HORSE_JUMB_STRENGTH, Material.DIAMOND_HORSE_ARMOR);
        registerCategory(bB, ZOMBIE_SPAWN_REINFORCEMENTS, Material.ZOMBIE_HEAD);

        registerMode(bB, AttributeModifier.Operation.ADD_NUMBER, "60b55f74681c68283a1c1ce51f1c83b52e2971c91ee34efcb598df3990a7e7");
        registerMode(bB, AttributeModifier.Operation.ADD_SCALAR, "57b1791bdc46d8a5c51729e8982fd439bb40513f64b5babee93294efc1c7");
        registerMode(bB, AttributeModifier.Operation.MULTIPLY_SCALAR_1, "a9f27d54ec5552c2ed8f8e1917e8a21cb98814cbb4bc3643c2f561f9e1e69f");

        registerAttributeSlot(bB, EquipmentSlot.HAND, Material.IRON_SWORD);
        registerAttributeSlot(bB, EquipmentSlot.OFF_HAND, Material.SHIELD);
        registerAttributeSlot(bB, EquipmentSlot.FEET, Material.IRON_BOOTS);
        registerAttributeSlot(bB, EquipmentSlot.LEGS, Material.IRON_LEGGINGS);
        registerAttributeSlot(bB, EquipmentSlot.CHEST, Material.IRON_CHESTPLATE);
        registerAttributeSlot(bB, EquipmentSlot.HEAD, Material.IRON_HELMET);
        bB.chatInput("attribute.set_amount").state(state -> state.icon(PlayerHeadUtils.getViaURL("461c8febcac21b9f63d87f9fd933589fe6468e93aa81cfcf5e52a4322e16e6"))
                        .render((holder, cache, btn, slot, itemStack) -> CallbackButtonRender.Result.of(Placeholder.unparsed("number", String.valueOf(holder.getGuiHandler().getCustomCache().getItems().getAttribAmount())))))
                .inputAction((guiHandler, player, s, args) -> {
                    try {
                        guiHandler.getCustomCache().getItems().setAttribAmount(Double.parseDouble(args[0]));
                    } catch (NumberFormatException e) {
                        creator.sendMessage(guiHandler, creator.translatedMsgKey("attribute.amount.error"));
                        return true;
                    }
                    return false;
                }).register();
        bB.chatInput("attribute.set_name").state(state -> state.icon(Material.NAME_TAG)
                        .render((holder, cache, btn, slot, itemStack) -> CallbackButtonRender.Result.of(Placeholder.unparsed("name", holder.getGuiHandler().getCustomCache().getItems().getAttributeName()))))
                .inputAction((guiHandler, player, s, args) -> {
                    guiHandler.getCustomCache().getItems().setAttributeName(args[0]);
                    return false;
                }).register();
        bB.chatInput("attribute.set_uuid").state(state -> state.icon(Material.TRIPWIRE_HOOK)
                        .render((holder, cache, btn, slot, itemStack) -> CallbackButtonRender.Result.of(Placeholder.unparsed("uuid", holder.getGuiHandler().getCustomCache().getItems().getAttributeUUID()))))
                .inputAction((guiHandler, player, s, args) -> {
                    try {
                        var uuid = UUID.fromString(args[0]);
                        guiHandler.getCustomCache().getItems().setAttributeUUID(uuid.toString());
                    } catch (IllegalArgumentException ex) {
                        creator.sendMessage(guiHandler, creator.translatedMsgKey("attribute.uuid.error.line1", Placeholder.unparsed("uuid", args[0])));
                        creator.sendMessage(guiHandler, creator.translatedMsgKey("attribute.uuid.error.line2"));
                        return true;
                    }
                    return false;
                }).register();
        bB.action("attribute.save").state(state -> state.icon(Material.GREEN_CONCRETE).action((holder, cache, btn, slot, details) -> {
            var itemMeta = cache.getItems().getItem().getItemMeta();
            itemMeta.addAttributeModifier(Attribute.valueOf(cache.getSubSetting().split("\\.")[1].toUpperCase(Locale.ROOT)), cache.getItems().getAttributeModifier());
            cache.getItems().getItem().setItemMeta(itemMeta);
            return ButtonInteractionResult.cancel(true);
        })).register();
        bB.action("attribute.delete").state(state -> state.icon(Material.RED_CONCRETE).action((holder, cache, btn, slot, details) -> {
            ChatUtils.sendAttributeModifierManager(holder.getPlayer());
            holder.getGuiHandler().close();
            return ButtonInteractionResult.cancel(true);
        })).register();
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
