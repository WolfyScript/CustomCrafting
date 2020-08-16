package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;

public class AttributeSlotButton extends ActionButton {

    public AttributeSlotButton(EquipmentSlot slot, Material material) {
        super("attribute.slot_"+slot.toString().toLowerCase(Locale.ROOT), new ButtonState("attribute.slot_"+slot.toString().toLowerCase(Locale.ROOT), material, new ButtonActionRender() {

            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                Items items = ((TestCache) guiHandler.getCustomCache()).getItems();
                items.setAttributeSlot(items.getAttributeSlot() == null ? slot : (items.getAttributeSlot().equals(slot) ? null : slot));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
                if (((TestCache) guiHandler.getCustomCache()).getItems().isAttributeSlot(slot)) {
                    return new ItemBuilder(itemStack).addEnchantment(Enchantment.DURABILITY, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS).create();
                }
                return itemStack;
            }
        }));
    }
}
