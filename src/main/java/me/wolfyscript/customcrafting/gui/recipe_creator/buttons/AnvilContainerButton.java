package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnvilContainerButton extends ItemInputButton {

    public AnvilContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("container_" + inputSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                TestCache cache = (TestCache) guiHandler.getCustomCache();
                CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
                if (event.isRightClick() && event.isShiftClick()) {
                    List<CustomItem> variants = new ArrayList<>();
                    if (anvilRecipe.getInput(inputSlot) != null) {
                        variants = anvilRecipe.getInput(inputSlot);
                    }
                    cache.getVariantsData().setSlot(inputSlot);
                    cache.getVariantsData().setVariants(variants);
                    guiHandler.changeToInv("variants");
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(customCrafting, () -> {
                        CustomItem input = !ItemUtils.isAirOrNull(inventory.getItem(slot)) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                        List<CustomItem> inputs = anvilRecipe.getInput(inputSlot);
                        if (inputs.size() > 0) {
                            inputs.set(0, input);
                        } else {
                            inputs.add(input);
                        }
                        anvilRecipe.setInput(inputSlot, inputs);
                    });
                }
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack item, int i, boolean b) {
                CustomAnvilRecipe anvilRecipe = ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe();
                if (anvilRecipe.getInput(inputSlot) != null && !anvilRecipe.getInput(inputSlot).isEmpty()) {
                    item = anvilRecipe.getInput(inputSlot).get(0).create();
                }
                return item;
            }
        }));
    }
}
