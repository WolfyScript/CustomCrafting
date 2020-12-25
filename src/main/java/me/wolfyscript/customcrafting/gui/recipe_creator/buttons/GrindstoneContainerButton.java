package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GrindstoneContainerButton extends ItemInputButton<CCCache> {

    public GrindstoneContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("grindstone.container_" + inputSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            GrindstoneRecipe grindstone = cache.getGrindstoneRecipe();

            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                List<CustomItem> variants = new ArrayList<>();

                switch (inputSlot) {
                    case 0:
                        if (!InventoryUtils.isCustomItemsListEmpty(grindstone.getInputTop())) {
                            variants = grindstone.getInputTop();
                        }
                        break;
                    case 1:
                        if (!InventoryUtils.isCustomItemsListEmpty(grindstone.getInputBottom())) {
                            variants = grindstone.getInputBottom();
                        }
                        break;
                    case 2:
                        if (!InventoryUtils.isCustomItemsListEmpty(grindstone.getResults())) {
                            variants = grindstone.getResults();
                        }
                }
                cache.getVariantsData().setSlot(inputSlot);
                cache.getVariantsData().setVariants(variants);
                guiHandler.openWindow("variants");
                return true;
            } else {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    CustomItem item = inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                    switch (inputSlot) {
                        case 0:
                            grindstone.setInputTop(item);
                            break;
                        case 1:
                            grindstone.setInputBottom(item);
                            break;
                        case 2:
                            grindstone.setResult(0, item);
                    }
                });
            }
            return false;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            GrindstoneRecipe grindstone = cache.getGrindstoneRecipe();
            switch (inputSlot) {
                case 0:
                    return !InventoryUtils.isCustomItemsListEmpty(grindstone.getInputTop()) ? grindstone.getInputTop().get(0).create() : new ItemStack(Material.AIR);
                case 1:
                    return !InventoryUtils.isCustomItemsListEmpty(grindstone.getInputBottom()) ? grindstone.getInputBottom().get(0).create() : new ItemStack(Material.AIR);
                case 2:
                    return !InventoryUtils.isCustomItemsListEmpty(grindstone.getResults()) ? grindstone.getResult().create() : new ItemStack(Material.AIR);
            }
            return itemStack == null ? new ItemStack(Material.AIR) : itemStack;
        }));
    }
}
