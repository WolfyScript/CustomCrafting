package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;

import java.util.Locale;

public class ButtonAttributeSlot extends ActionButton<CCCache> {

    public ButtonAttributeSlot(EquipmentSlot equipmentSlot, Material material) {
        super("attribute.slot_"+equipmentSlot.toString().toLowerCase(Locale.ROOT), material, (cache, guiHandler, player, inventory, slot, event) -> {
            Items items = guiHandler.getCustomCache().getItems();
            items.setAttributeSlot(items.getAttributeSlot() == null ? equipmentSlot : (items.getAttributeSlot().equals(equipmentSlot) ? null : equipmentSlot));
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            if (guiHandler.getCustomCache().getItems().isAttributeSlot(equipmentSlot)) {
                return new ItemBuilder(itemStack).addEnchantment(Enchantment.DURABILITY, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS).create();
            }
            return itemStack;
        });
    }
}
