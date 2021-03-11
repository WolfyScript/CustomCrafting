package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class StonecutterResultButton extends ItemInputButton<CCCache> {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getStonecutterRecipe().setResult(Collections.singletonList(items.getItem()));

    public StonecutterResultButton(CustomCrafting customCrafting) {
        super("stonecutter.result", new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            CustomStonecutterRecipe stonecutter = cache.getStonecutterRecipe();
            if (event instanceof InventoryClickEvent) {
                return ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick();
            }
            return false;
        }, (cache, guiHandler, player, inventory, itemStack, i, event) -> {
            CustomStonecutterRecipe stonecutter = cache.getStonecutterRecipe();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                if (!ItemUtils.isAirOrNull(itemStack)) {
                    cache.getItems().setItem(true, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR));
                    cache.setApplyItem(APPLY_ITEM);
                    guiHandler.openWindow("item_editor");
                }
            } else {
                stonecutter.setResult(Collections.singletonList(!ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR)));
            }
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CustomStonecutterRecipe stonecutter = guiHandler.getCustomCache().getStonecutterRecipe();
            return !ItemUtils.isAirOrNull(stonecutter.getResult()) ? stonecutter.getResult().create() : new ItemStack(Material.AIR);
        }));
    }
}
