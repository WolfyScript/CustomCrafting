package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class CauldronContainerButton extends ItemInputButton<CCCache> {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getCauldronRecipe().setResult(Collections.singletonList(items.getItem()));

    public CauldronContainerButton(int inputSlot) {
        super("cauldron.container_" + inputSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            CauldronRecipe recipe = cache.getCauldronRecipe();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                if (inputSlot == 0 && recipe.getIngredients() != null) {
                    cache.getVariantsData().setSlot(inputSlot);
                    cache.getVariantsData().setVariants(recipe.getIngredients());
                    guiHandler.openWindow("variants");
                } else if (inputSlot == 1 && recipe.getResults() != null) {
                    if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                        cache.getItems().setItem(true, inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR));
                        cache.setApplyItem(APPLY_ITEM);
                        guiHandler.openWindow(new NamespacedKey("none", "item_editor"));
                    }
                }
                return true;
            }
            return false;
        }, (cache, guiHandler, player, inventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (!event.getView().getBottomInventory().equals(((InventoryClickEvent) event).getClickedInventory())) {
                    return;
                }
            }
            CauldronRecipe recipe = cache.getCauldronRecipe();
            CustomItem input = !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR);
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
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CauldronRecipe recipe = guiHandler.getCustomCache().getCauldronRecipe();
            List<CustomItem> items = inputSlot == 0 ? recipe.getIngredients() : recipe.getResults();
            return !InventoryUtils.isCustomItemsListEmpty(items) ? items.get(0).create() : new ItemStack(Material.AIR);
        }));
    }
}
