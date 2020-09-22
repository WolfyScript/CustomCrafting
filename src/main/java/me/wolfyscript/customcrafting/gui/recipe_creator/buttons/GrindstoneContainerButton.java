package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GrindstoneContainerButton extends ItemInputButton {

    public GrindstoneContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("grindstone.container_" + inputSlot, new ButtonState("", Material.AIR, (guiHandler, player, inventory, slot, event) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            GrindstoneRecipe grindstone = cache.getGrindstoneRecipe();

            if (event.isRightClick() && event.isShiftClick()) {
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
                guiHandler.changeToInv("variants");
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
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
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
