package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingConfig;
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

public class BrewingContainerButton extends ItemInputButton {

    public BrewingContainerButton(int recipeSlot, CustomCrafting customCrafting) {
        super("brewing.container_" + recipeSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                TestCache cache = (TestCache) guiHandler.getCustomCache();
                BrewingConfig brewingConfig = cache.getBrewingConfig();
                if (event.isRightClick() && event.isShiftClick()) {
                    List<CustomItem> variants = new ArrayList<>();
                    if (recipeSlot == 0) {
                        if (brewingConfig.getIngredient() != null) {
                            variants = brewingConfig.getIngredient();
                        }
                    } else {
                        if (brewingConfig.getAllowedItems() != null) {
                            variants = brewingConfig.getAllowedItems();
                        }
                    }
                    cache.getVariantsData().setSlot(recipeSlot);
                    cache.getVariantsData().setVariants(variants);
                    guiHandler.changeToInv("variants");
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(customCrafting, () -> {
                        CustomItem customItem = new CustomItem(Material.AIR);
                        if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                            customItem = CustomItem.getByItemStack(inventory.getItem(slot));
                        }
                        List<CustomItem> inputs;
                        if (recipeSlot == 0) {
                            inputs = brewingConfig.getIngredient();
                            if (inputs.size() > 0) {
                                inputs.set(0, customItem);
                            } else {
                                inputs.add(customItem);
                            }
                            brewingConfig.setIngredient(inputs);
                        } else {
                            inputs = brewingConfig.getAllowedItems();
                            if (inputs.size() > 0) {
                                inputs.set(0, customItem);
                            } else {
                                inputs.add(customItem);
                            }
                            brewingConfig.setAllowedItems(inputs);
                        }
                    });
                }
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                BrewingConfig brewingConfig = ((TestCache) guiHandler.getCustomCache()).getBrewingConfig();
                itemStack = new ItemStack(Material.AIR);
                if (recipeSlot == 0) {
                    if (brewingConfig.getIngredient() != null && !brewingConfig.getIngredient().isEmpty()) {
                        itemStack = brewingConfig.getIngredient().get(0).getRealItem();
                    }
                } else {
                    if (brewingConfig.getAllowedItems() != null && !brewingConfig.getAllowedItems().isEmpty()) {
                        itemStack = brewingConfig.getAllowedItems().get(0).getRealItem();
                    }
                }
                return itemStack;
            }
        }));
    }
}
