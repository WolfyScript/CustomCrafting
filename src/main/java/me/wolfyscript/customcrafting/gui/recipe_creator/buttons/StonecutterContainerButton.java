package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StonecutterContainerButton extends ItemInputButton<TestCache> {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getStonecutterRecipe().setResult(Collections.singletonList(items.getItem()));

    public StonecutterContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("stonecutter.container_" + inputSlot, new ButtonState<>("", Material.AIR, (guiHandler, player, inventory, slot, event) -> {
            TestCache cache = guiHandler.getCustomCache();
            CustomStonecutterRecipe stonecutter = cache.getStonecutterRecipe();
            if (inputSlot == 1) {
                //RESULT STUFF
                if (event.isRightClick() && event.isShiftClick()) {
                    Bukkit.getScheduler().runTask(customCrafting, () -> {
                        if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                            cache.getItems().setItem(true, !ItemUtils.isAirOrNull(inventory.getItem(slot)) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR));
                            cache.setApplyItem(APPLY_ITEM);
                            guiHandler.changeToInv("item_editor");
                        }
                    });
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(customCrafting, () -> stonecutter.setResult(Collections.singletonList(!ItemUtils.isAirOrNull(inventory.getItem(slot)) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR))));
                }
            } else {
                if (event.isRightClick() && event.isShiftClick()) {
                    List<CustomItem> variants = new ArrayList<>();
                    if (stonecutter.getSource() != null) {
                        variants = stonecutter.getSource();
                    }
                    cache.getVariantsData().setSlot(inputSlot);
                    cache.getVariantsData().setVariants(variants);
                    guiHandler.changeToInv("variants");
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(customCrafting, () -> stonecutter.setSource(0, !ItemUtils.isAirOrNull(inventory.getItem(slot)) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR)));
                }
            }
            return false;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
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
