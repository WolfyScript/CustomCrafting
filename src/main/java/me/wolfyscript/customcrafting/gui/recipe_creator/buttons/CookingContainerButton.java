package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class CookingContainerButton extends ItemInputButton {

    public CookingContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("cooking.container_" + inputSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                TestCache cache = ((TestCache) guiHandler.getCustomCache());
                CustomCookingRecipe<?> cooking = cache.getCookingRecipe();
                if (event.isRightClick() && event.isShiftClick()) {
                    List<CustomItem> variants;
                    if(inputSlot == 0){
                        variants = cooking.getSource();
                    }else{
                        variants = cooking.getCustomResults();
                    }
                    cache.getVariantsData().setSlot(inputSlot);
                    cache.getVariantsData().setVariants(variants);
                    guiHandler.changeToInv("variants");
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(customCrafting, () -> {
                        CustomItem customItem = !ItemUtils.isAirOrNull(inventory.getItem(slot)) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                        if(inputSlot == 0){
                            cooking.setSource(0, customItem);
                        }else{
                            cooking.setResult(0, customItem);
                        }
                    });
                }
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
                CustomCookingRecipe<?> cooking = ((TestCache) guiHandler.getCustomCache()).getCookingRecipe();
                if (inputSlot == 0) {
                    return !InventoryUtils.isCustomItemsListEmpty(cooking.getSource()) ? cooking.getSource().get(0).create() : new ItemStack(Material.AIR);
                } else if (inputSlot == 1) {
                    return !ItemUtils.isAirOrNull(cooking.getCustomResult()) ? cooking.getCustomResult().create() : new ItemStack(Material.AIR);
                }
                return itemStack;
            }
        }));
    }
}
