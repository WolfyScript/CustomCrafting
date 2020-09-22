package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;

import java.util.Locale;

public class AttributeSlotButton extends ActionButton {

    public AttributeSlotButton(EquipmentSlot slot, Material material) {
        super("attribute.slot_"+slot.toString().toLowerCase(Locale.ROOT), material, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Items items = ((TestCache) guiHandler.getCustomCache()).getItems();
            items.setAttributeSlot(items.getAttributeSlot() == null ? slot : (items.getAttributeSlot().equals(slot) ? null : slot));
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            if (((TestCache) guiHandler.getCustomCache()).getItems().isAttributeSlot(slot)) {
                return new ItemBuilder(itemStack).addEnchantment(Enchantment.DURABILITY, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS).create();
            }
            return itemStack;
        });
    }
}
