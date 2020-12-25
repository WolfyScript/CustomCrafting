package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StonecutterContainerButton extends ItemInputButton<CCCache> {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getStonecutterRecipe().setResult(Collections.singletonList(items.getItem()));

    public StonecutterContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("stonecutter.container_" + inputSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            CustomStonecutterRecipe stonecutter = cache.getStonecutterRecipe();
            if (event instanceof InventoryClickEvent) {
                if (inputSlot == 1) {
                    return ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick();
                } else if (((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                    List<CustomItem> variants = new ArrayList<>();
                    if (stonecutter.getSource() != null) {
                        variants = stonecutter.getSource();
                    }
                    cache.getVariantsData().setSlot(inputSlot);
                    cache.getVariantsData().setVariants(variants);
                    guiHandler.openWindow("variants");
                    return true;
                }
            }
            return false;
        }, (cache, guiHandler, player, inventory, itemStack, i, event) -> {
            CustomStonecutterRecipe stonecutter = cache.getStonecutterRecipe();
            if (inputSlot == 1) {
                //RESULT STUFF
                if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                    if (!ItemUtils.isAirOrNull(itemStack)) {
                        cache.getItems().setItem(true, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR));
                        cache.setApplyItem(APPLY_ITEM);
                        guiHandler.openWindow("item_editor");
                    }
                } else {
                    stonecutter.setResult(Collections.singletonList(!ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR)));
                }
            } else if (!(event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick())) {
                stonecutter.setSource(0, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR));
            }
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CustomStonecutterRecipe stonecutter = guiHandler.getCustomCache().getStonecutterRecipe();
            if (inputSlot == 1) {
                //RESULT STUFF
                return !ItemUtils.isAirOrNull(stonecutter.getResult()) ? stonecutter.getResult().create() : new ItemStack(Material.AIR);
            } else {
                return !InventoryUtils.isCustomItemsListEmpty(stonecutter.getSource()) ? stonecutter.getSource().get(0).create() : new ItemStack(Material.AIR);
            }
        }));
    }
}
