package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ButtonRecipeResult extends ItemInputButton<CCCache> {

    public ButtonRecipeResult() {
        super("recipe.result", new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            Result<?> result = cache.getRecipe().getResult();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                guiHandler.openWindow("result");
                return true;
            }
            return result.getItems().isEmpty() && !result.getTags().isEmpty();
        }, (cache, guiHandler, player, inventory, itemStack, i, event) -> {
            Result<?> result = cache.getRecipe().getResult();
            if (result.getItems().isEmpty() && !result.getTags().isEmpty()) {
                return;
            }
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                return;
            }
            result.put(0, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : null);
            result.buildChoices();
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            Result<?> result = cache.getRecipe().getResult();
            return result != null ? result.getItemStack() : ItemUtils.AIR;
        }));
    }
}
