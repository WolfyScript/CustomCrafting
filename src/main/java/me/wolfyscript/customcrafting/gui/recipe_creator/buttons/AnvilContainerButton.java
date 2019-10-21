package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Anvil;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonRender;
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

public class AnvilContainerButton extends ItemInputButton {

    public AnvilContainerButton(int inputSlot) {
        super("anvil.container_" + inputSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                PlayerCache cache = CustomCrafting.getPlayerCache(player);
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
                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        CustomItem input = inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                        if (anvilRecipe.getInput(slot).size() > 0) {
                            anvilRecipe.getInput(slot).set(0, input);
                        } else {
                            anvilRecipe.getInput(slot).add(input);
                        }
                    });
                }
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack item, int i, boolean b) {
                CustomAnvilRecipe anvilRecipe = CustomCrafting.getPlayerCache(player).getAnvilRecipe();
                if (anvilRecipe.getInput(inputSlot) != null && !anvilRecipe.getInput(inputSlot).isEmpty()) {
                    item = anvilRecipe.getInput(inputSlot).get(0);
                }
                return item;
            }
        }));
    }
}
