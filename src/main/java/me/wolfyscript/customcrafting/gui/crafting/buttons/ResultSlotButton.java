package me.wolfyscript.customcrafting.gui.crafting.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbench;
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
                if(inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)){
                    RecipeUtils.consumeRecipe(eliteWorkbenchData.getResult(), eliteWorkbenchData.getContents(), event);
                    RecipeUtils.getPrecraftedRecipes().put(event.getWhoClicked().getUniqueId(), null);
                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        EliteWorkbench eliteWorkbench = eliteWorkbenchData.getEliteWorkbench();
                        ItemStack result = RecipeUtils.preCheckRecipe(eliteWorkbenchData.getContents(), player, false, inventory, true, eliteWorkbench != null && eliteWorkbench.isAdvancedRecipes());
                        if(result != null){
                            eliteWorkbenchData.setResult(result);
                        }
                    });
                }
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
