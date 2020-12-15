package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class CauldronContainerButton extends ItemInputButton {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getCauldronRecipe().setResult(Collections.singletonList(items.getItem()));

    public CauldronContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("cauldron.container_" + inputSlot, new ButtonState("", Material.AIR, (guiHandler, player, inventory, slot, event) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            CauldronRecipe recipe = cache.getCauldronRecipe();
            if (event.isRightClick() && event.isShiftClick()) {
                if (inputSlot == 0 && recipe.getIngredients() != null) {
                    cache.getVariantsData().setSlot(inputSlot);
                    cache.getVariantsData().setVariants(recipe.getIngredients());
                    guiHandler.changeToInv("variants");
                } else if (inputSlot == 1 && recipe.getResults() != null) {
                    if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                        cache.getItems().setItem(true, inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR));
                        cache.setApplyItem(APPLY_ITEM);
                        guiHandler.changeToInv(new NamespacedKey("none", "item_editor"));
                    }
                }
                return true;
            } else {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    CustomItem input = inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                    if (inputSlot == 0) {
                        List<CustomItem> inputs = recipe.getIngredients();
                        if (inputs.size() > 0) {
                            inputs.set(0, input);
                        } else {
                            inputs.add(input);
                        }
                        recipe.setIngredients(inputs);
                    } else {
                        recipe.setResult(Collections.singletonList(input));
                    }
                });
            }
            return false;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            CauldronRecipe recipe = ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe();
            List<CustomItem> items = inputSlot == 0 ? recipe.getIngredients() : recipe.getResults();
            return !InventoryUtils.isCustomItemsListEmpty(items) ? items.get(0).create() : new ItemStack(Material.AIR);
        }));
    }
}
