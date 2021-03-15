package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ButtonContainerItemResult extends ItemInputButton<CCCache> {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getRecipe().getResult().put(items.getVariantSlot(), CustomItem.getReferenceByItemStack(customItem.create()));

    public ButtonContainerItemResult(int variantSlot) {
        super("variant_container_" + variantSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                    cache.getItems().setVariant(variantSlot, CustomItem.getReferenceByItemStack(inventory.getItem(slot)));
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
            cache.getRecipe().getResult().put(variantSlot, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR));
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            Result<?> result = cache.getRecipe().getResult();
            return result != null ? result.getItemStack(variantSlot) : ItemUtils.AIR;
        }));
    }
}
