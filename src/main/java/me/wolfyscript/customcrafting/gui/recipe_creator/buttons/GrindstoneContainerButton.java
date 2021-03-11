package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GrindstoneContainerButton extends ItemInputButton<CCCache> {

    public GrindstoneContainerButton(CustomCrafting customCrafting) {
        super("grindstone.result", new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            GrindstoneRecipe grindstone = cache.getGrindstoneRecipe();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                cache.getVariantsData().setSlot(2);
                cache.getVariantsData().setVariants(!InventoryUtils.isCustomItemsListEmpty(grindstone.getResults()) ? grindstone.getResults() : new ArrayList<>());
                guiHandler.openWindow("variants");
                return true;
            } else {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    CustomItem item = inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                    grindstone.setResult(0, item);
                });
            }
            return false;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            GrindstoneRecipe grindstone = cache.getGrindstoneRecipe();
            return grindstone.getResult() != null ? grindstone.getResult().create() : new ItemStack(Material.AIR);
        }));
    }
}
