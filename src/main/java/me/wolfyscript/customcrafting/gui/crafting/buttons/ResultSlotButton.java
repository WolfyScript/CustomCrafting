package me.wolfyscript.customcrafting.gui.crafting.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbenchData;
import me.wolfyscript.customcrafting.recipes.RecipeUtils;
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

import java.util.HashMap;

public class ResultSlotButton extends ItemInputButton {

    public ResultSlotButton() {
        super("result_slot", new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                PlayerCache cache = CustomCrafting.getPlayerCache(player);
                EliteWorkbenchData eliteWorkbenchData = cache.getEliteWorkbenchData();
                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                    eliteWorkbenchData.setResult(inventory.getItem(slot) == null ? new ItemStack(Material.AIR) : inventory.getItem(slot));
                    if(inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)){
                        ItemStack[] matrix = eliteWorkbenchData.getContents();
                        RecipeUtils.consumeRecipe(eliteWorkbenchData.getResult(), matrix, event);
                        guiHandler.getCurrentInv().update(guiHandler);
                        ItemStack result = RecipeUtils.preCheckRecipe(eliteWorkbenchData.getContents(), player, false, inventory);
                        if(result != null){
                            eliteWorkbenchData.setResult(result);
                        }
                    }
                }, 2);
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                PlayerCache cache = CustomCrafting.getPlayerCache(player);
                EliteWorkbenchData eliteWorkbenchData = cache.getEliteWorkbenchData();
                if(eliteWorkbenchData.getResult() != null){
                    itemStack = eliteWorkbenchData.getResult();
                }
                return itemStack;
            }
        }));
    }
}
