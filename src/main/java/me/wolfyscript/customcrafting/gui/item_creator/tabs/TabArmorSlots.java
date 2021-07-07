package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.ArmorSlotToggleButton;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.OptionButton;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class TabArmorSlots extends ItemCreatorTab {

    public static final String KEY = "armor_slots";

    public TabArmorSlots() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register() {
        creator.registerButton(new OptionButton(Material.IRON_HELMET, this));
        creator.registerButton(new ArmorSlotToggleButton(EquipmentSlot.HEAD, Material.DIAMOND_HELMET));
        creator.registerButton(new ArmorSlotToggleButton(EquipmentSlot.CHEST, Material.DIAMOND_CHESTPLATE));
        creator.registerButton(new ArmorSlotToggleButton(EquipmentSlot.LEGS, Material.DIAMOND_LEGGINGS));
        creator.registerButton(new ArmorSlotToggleButton(EquipmentSlot.FEET, Material.DIAMOND_BOOTS));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(28, "armor_slots.head");
        update.setButton(30, "armor_slots.chest");
        update.setButton(32, "armor_slots.legs");
        update.setButton(34, "armor_slots.feet");
    }
}
