package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
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

public class AnvilContainerButton extends ItemInputButton {

    public AnvilContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("container_" + inputSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                TestCache cache = (TestCache) guiHandler.getCustomCache();
                CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
                List<CustomItem> items = inputSlot == 2 ? anvilRecipe.getCustomResults() : anvilRecipe.getInput(inputSlot);
                if (event.isRightClick() && event.isShiftClick()) {
                    cache.getVariantsData().setSlot(inputSlot);
                    cache.getVariantsData().setVariants(items);
                    guiHandler.changeToInv("variants");
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(customCrafting, () -> {
                        CustomItem input = !ItemUtils.isAirOrNull(inventory.getItem(slot)) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                        if (items.size() > 0) {
                            items.set(0, input);
                        } else {
                            items.add(input);
                        }
                        if(inputSlot == 2){
                            anvilRecipe.setResult(items);
                        }else{
                            anvilRecipe.setInput(inputSlot, items);
                        }
                    });
                }
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack item, int i, boolean b) {
                CustomAnvilRecipe anvilRecipe = ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe();
                List<CustomItem> items = inputSlot == 2 ? anvilRecipe.getCustomResults() : anvilRecipe.getInput(inputSlot);
                return InventoryUtils.isCustomItemsListEmpty(items) ? new ItemStack(Material.AIR) : items.get(0).create();
            }
        }));
    }
}
