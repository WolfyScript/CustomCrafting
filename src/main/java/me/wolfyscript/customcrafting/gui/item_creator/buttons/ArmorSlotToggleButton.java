package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;

import java.util.Locale;

public class ArmorSlotToggleButton extends ToggleButton {

    public ArmorSlotToggleButton(EquipmentSlot slot, Material material) {
        super("armor_slots." + slot.toString().toLowerCase(Locale.ROOT), new ButtonState("armor_slots." + slot.toString().toLowerCase(Locale.ROOT) + ".enabled", new ItemBuilder(material).addEnchantment(Enchantment.DURABILITY, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS).create(), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().removeEquipmentSlots(slot);
            return true;
        }), new ButtonState("armor_slots." + slot.toString().toLowerCase(Locale.ROOT) + ".disabled", material, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().addEquipmentSlots(slot);
            return true;
        }));
    }
}
