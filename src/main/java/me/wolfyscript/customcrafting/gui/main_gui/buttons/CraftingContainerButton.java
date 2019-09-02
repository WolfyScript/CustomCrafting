package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.customcrafting.items.CustomItem;
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

public class CraftingContainerButton extends ItemInputButton {

    public CraftingContainerButton(int recipeSlot) {
        super("crafting.container_" + recipeSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                PlayerCache cache = CustomCrafting.getPlayerCache(player);
                Workbench workbench = cache.getWorkbench();
                if (event.isRightClick() && event.isShiftClick()) {
                    List<CustomItem> variants = new ArrayList<>();
                    if (recipeSlot == 9) {
                        if (workbench.getResult() != null) {
                            variants = workbench.getResult();
                        }
                    } else if (workbench.getIngredients(recipeSlot) != null) {
                        variants = workbench.getIngredients(recipeSlot);
                    }
                    cache.getVariantsData().setSlot(recipeSlot);
                    cache.getVariantsData().setVariants(variants);
                    guiHandler.changeToInv("variants");
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        CustomItem customItem = inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                        if (recipeSlot == 9) {
                            workbench.setResult(0, customItem);
                        } else {
                            workbench.setIngredient(recipeSlot, 0, customItem);
                        }
                    });
                }
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
                if (recipeSlot == 9) {
                    if (workbench.getResult() != null && !workbench.getResult().isEmpty()) {
                        itemStack = workbench.getResult().get(0).getIDItem(workbench.getResultCustomAmount());
                    }
                } else if (workbench.getIngredient(recipeSlot) != null) {
                    itemStack = workbench.getIngredient(recipeSlot).getIDItem();
                }
                return itemStack;
            }
        }));
    }
}
