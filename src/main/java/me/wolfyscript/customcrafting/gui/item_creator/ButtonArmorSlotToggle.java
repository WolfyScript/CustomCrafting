package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;

import java.util.Locale;

public class ButtonArmorSlotToggle extends ToggleButton<CCCache> {

    private static String key(EquipmentSlot slot) {
        return "armor_slots." + slot.toString().toLowerCase(Locale.ROOT);
    }

    public ButtonArmorSlotToggle(EquipmentSlot slot, Material material) {
        super(key(slot), (cache, guiHandler, player, guiInventory, i) -> {
            CustomItem item = cache.getItems().getItem();
            return !ItemUtils.isAirOrNull(item) && item.hasEquipmentSlot(slot);
        }, new ButtonState<>(key(slot) + ".enabled", new ItemBuilder(material).addEnchantment(Enchantment.DURABILITY, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS).create(), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().removeEquipmentSlots(slot);
            return true;
        }), new ButtonState<>(key(slot) + ".disabled", material, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().addEquipmentSlots(slot);
            return true;
        }));
    }
}
