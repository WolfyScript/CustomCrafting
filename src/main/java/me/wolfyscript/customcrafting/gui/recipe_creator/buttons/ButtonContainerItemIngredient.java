package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ButtonContainerItemIngredient extends ActionButton<CCCache> {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getIngredientData().getIngredient().put(items.getVariantSlot(), CustomItem.getReferenceByItemStack(customItem.create()));

    public ButtonContainerItemIngredient(int slot) {
        super("item_container_" + slot, Material.AIR, (cache, guiHandler, player, inventory, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                    cache.getItems().setVariant(slot, CustomItem.getReferenceByItemStack(inventory.getItem(slot)));
                    cache.setApplyItem(APPLY_ITEM);
                    guiHandler.openWindow(new NamespacedKey("none", "item_editor"));
                }
                return true;
            }
            return false;
        }, (cache, guiHandler, player, guiInventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                return;
            }
            cache.getIngredientData().getIngredient().put(slot, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR));
        }, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            Ingredient data = cache.getIngredientData().getIngredient();
            return data != null ? data.getItemStack(slot) : ItemUtils.AIR;
        }, (cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
        });
    }

}
