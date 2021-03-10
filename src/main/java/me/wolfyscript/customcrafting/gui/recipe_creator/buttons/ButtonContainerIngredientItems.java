package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.IngredientData;
import me.wolfyscript.customcrafting.utils.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class ButtonContainerIngredientItems extends ActionButton<CCCache> {

    public ButtonContainerIngredientItems(int slot) {
        super("item_container_" + slot, Material.AIR, (cache, guiHandler, player, inventory, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                    IngredientData data = cache.getIngredientData();
                    Ingredient ingredient = data.getIngredient();
                }
                return true;
            }
            return false;
        }, (cache, guiHandler, player, guiInventory, itemStack, i, inventoryInteractEvent) -> {
            IngredientData data = cache.getIngredientData();


            data.put(slot, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR));
        }, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            IngredientData data = cache.getIngredientData();
            List<APIReference> refs = data.getIngredient().getItems();
            if (refs.size() > slot) {
                APIReference ref = refs.get(slot);
                return ref != null ? CustomItem.with(ref).create() : ItemUtils.AIR;
            }
            return ItemUtils.AIR;
        }, (cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
        });
    }

}
