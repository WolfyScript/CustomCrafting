package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.VariantsData;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class VariantContainerButton extends ItemInputButton<CCCache> {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getVariantsData().putVariant(items.getVariantSlot(), CustomItem.getReferenceByItemStack(customItem.create()));

    public VariantContainerButton(int variantSlot, CustomCrafting customCrafting) {
        super("variant_container_" + variantSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                    cache.getItems().setVariant(variantSlot, CustomItem.getReferenceByItemStack(inventory.getItem(slot)));
                    cache.setApplyItem(APPLY_ITEM);
                    guiHandler.openWindow(new NamespacedKey("none", "item_editor"));
                }
                return true;
            }
            return false;
        }, (cache, guiHandler, player, guiInventory, itemStack, i, event) -> {
            VariantsData variantsData = cache.getVariantsData();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                return;
            }
            variantsData.putVariant(variantSlot, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR));
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            VariantsData variantsData = guiHandler.getCustomCache().getVariantsData();
            return variantsData.getVariants() != null && variantsData.getVariants().size() > variantSlot ? variantsData.getVariants().get(variantSlot).getIDItem() : new ItemStack(Material.AIR);
        }));
    }
}
