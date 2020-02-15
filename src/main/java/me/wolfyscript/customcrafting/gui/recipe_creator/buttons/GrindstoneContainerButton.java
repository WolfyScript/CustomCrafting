package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneConfig;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GrindstoneContainerButton extends ItemInputButton {

    public GrindstoneContainerButton(int inputSlot) {
        super("grindstone.container_" + inputSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                TestCache cache = ((TestCache) guiHandler.getCustomCache());
                GrindstoneConfig grindstone = cache.getGrindstoneConfig();

                if (event.isRightClick() && event.isShiftClick()) {
                    List<CustomItem> variants = new ArrayList<>();

                    switch (inputSlot) {
                        case 0:
                            if (grindstone.getInputTop() != null) {
                                variants = grindstone.getInputTop();
                            }
                            break;
                        case 1:
                            if (grindstone.getInputBottom() != null) {
                                variants = grindstone.getInputBottom();
                            }
                            break;
                        case 2:
                            if (grindstone.getResult() != null) {
                                variants = grindstone.getResult();
                            }
                    }
                    cache.getVariantsData().setSlot(inputSlot);
                    cache.getVariantsData().setVariants(variants);
                    guiHandler.changeToInv("variants");
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        CustomItem item = inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                        switch (inputSlot) {
                            case 0:
                                grindstone.setInputTop(0, item);
                                break;
                            case 1:
                                grindstone.setInputBottom(0, item);
                                break;
                            case 2:
                                List<CustomItem> results = grindstone.getResult();
                                if (results.size() > 0) {
                                    results.set(0, item);
                                } else {
                                    results.add(item);
                                }
                                grindstone.setResult(results);
                        }
                    });
                }
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
                TestCache cache = ((TestCache) guiHandler.getCustomCache());
                GrindstoneConfig grindstone = cache.getGrindstoneConfig();
                switch (inputSlot) {
                    case 0:
                        if (grindstone.getInputTop() != null && !grindstone.getInputTop().isEmpty()) {
                            itemStack = grindstone.getInputTop().get(0);
                        }
                        break;
                    case 1:
                        if (grindstone.getInputBottom() != null && !grindstone.getInputBottom().isEmpty()) {
                            itemStack = grindstone.getInputBottom().get(0);
                        }
                        break;
                    case 2:
                        if (grindstone.getResult() != null && !grindstone.getResult().isEmpty()) {
                            itemStack = grindstone.getResult().get(0);
                        }
                }
                if (itemStack == null) {
                    itemStack = new ItemStack(Material.AIR);
                }
                return itemStack;
            }
        }));
    }
}
