package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CookingContainerButton extends ItemInputButton<CCCache> {

    public CookingContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("cooking.container_" + inputSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            CustomCookingRecipe<?, ?> cooking = cache.getCookingRecipe();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                List<CustomItem> variants;
                if(inputSlot == 0){
                    variants = cooking.getSource();
                }else{
                    variants = cooking.getResults();
                }
                cache.getVariantsData().setSlot(inputSlot);
                cache.getVariantsData().setVariants(variants);
                guiHandler.openWindow("variants");
                return true;
            }
            return false;
        }, (cache, guiHandler, player, guiInventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (!event.getView().getBottomInventory().equals(((InventoryClickEvent) event).getClickedInventory())) {
                    return;
                }
            }
            CustomCookingRecipe<?, ?> cooking = cache.getCookingRecipe();
            CustomItem customItem = !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR);
            if(inputSlot == 0){
                cooking.setSource(0, customItem);
            }else{
                cooking.setResult(0, customItem);
            }

        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CustomCookingRecipe<?, ?> cooking = guiHandler.getCustomCache().getCookingRecipe();
            if (inputSlot == 0) {
                return !InventoryUtils.isCustomItemsListEmpty(cooking.getSource()) ? cooking.getSource().get(0).create() : new ItemStack(Material.AIR);
            } else if (inputSlot == 1) {
                return !ItemUtils.isAirOrNull(cooking.getResult()) ? cooking.getResult().create() : new ItemStack(Material.AIR);
            }
            return itemStack;
        }));
    }
}
