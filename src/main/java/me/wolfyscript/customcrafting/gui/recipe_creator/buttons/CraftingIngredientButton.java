package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftConfig;
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

public class CraftingIngredientButton extends ItemInputButton {

    public CraftingIngredientButton(int recipeSlot) {
        super("crafting.container_" + recipeSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                PlayerCache cache = CustomCrafting.getPlayerCache(player);
                CraftConfig workbench = cache.getCraftConfig();
                if (event.isRightClick() && event.isShiftClick()) {
                    List<CustomItem> variants = new ArrayList<>();
                    if ((workbench instanceof EliteCraftConfig && recipeSlot == 36) || (!(workbench instanceof EliteCraftConfig) && recipeSlot == 9)) {
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
                        CustomItem customItem = new CustomItem(Material.AIR);
                        if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                            customItem = CustomItem.getByItemStack(inventory.getItem(slot));
                        }
                        if ((workbench instanceof EliteCraftConfig && recipeSlot == 36) || (!(workbench instanceof EliteCraftConfig) && recipeSlot == 9)) {
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
                CraftConfig workbench = CustomCrafting.getPlayerCache(player).getCraftConfig();
                itemStack = new ItemStack(Material.AIR);
                if ((workbench instanceof EliteCraftConfig && recipeSlot == 36) || (!(workbench instanceof EliteCraftConfig) && recipeSlot == 9)) {
                    if (workbench.getResult() != null && !workbench.getResult().isEmpty()) {
                        itemStack = workbench.getResult().get(0).getRealItem();
                    }
                } else if (workbench.getIngredient(recipeSlot) != null) {
                    itemStack = workbench.getIngredient(recipeSlot).getRealItem();
                }
                return itemStack;
            }
        }));
    }
}
