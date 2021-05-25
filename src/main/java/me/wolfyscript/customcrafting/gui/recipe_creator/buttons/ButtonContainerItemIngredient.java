package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ButtonContainerItemIngredient extends ItemInputButton<CCCache> {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getIngredientData().getIngredient().put(items.getVariantSlot(), CustomItem.getReferenceByItemStack(customItem.create()));

    public ButtonContainerItemIngredient(int ingredSlot) {
        super("item_container_" + ingredSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, invSlot, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (!ItemUtils.isAirOrNull(inventory.getItem(invSlot))) {
                    cache.getItems().setVariant(ingredSlot, CustomItem.getReferenceByItemStack(inventory.getItem(invSlot)));
                    cache.setApplyItem(APPLY_ITEM);
                    guiHandler.openWindow(RecipeCreatorCluster.ITEM_EDITOR);
                }
                return true;
            }
            return false;
        }, (cache, guiHandler, player, guiInventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                return;
            }
            cache.getIngredientData().getIngredient().put(ingredSlot, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR));
        }, null, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            Ingredient data = cache.getIngredientData().getIngredient();
            return data != null ? data.getItemStack(ingredSlot) : ItemUtils.AIR;
        }));
    }

}
