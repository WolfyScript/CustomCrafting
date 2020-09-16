package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.VariantsData;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class VariantContainerButton extends ItemInputButton {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getVariantsData().putVariant(items.getVariantSlot(), customItem);

    public VariantContainerButton(int variantSlot, CustomCrafting customCrafting) {
        super("variant_container_" + variantSlot, new ButtonState("", Material.AIR, (guiHandler, player, inventory, slot, event) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            VariantsData variantsData = cache.getVariantsData();
            if (event.getClick().equals(ClickType.SHIFT_RIGHT)) {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                        cache.getItems().setVariant(variantSlot, CustomItem.getReferenceByItemStack(inventory.getItem(slot)));
                        cache.setApplyItem(APPLY_ITEM);
                        guiHandler.changeToInv("none", "item_editor");
                    }
                });
                return true;
            }
            Bukkit.getScheduler().runTask(customCrafting, () -> variantsData.putVariant(variantSlot, !ItemUtils.isAirOrNull(inventory.getItem(slot)) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR)));
            return false;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            VariantsData variantsData = ((TestCache) guiHandler.getCustomCache()).getVariantsData();
            return variantsData.getVariants() != null && variantsData.getVariants().size() > variantSlot ? variantsData.getVariants().get(variantSlot).getIDItem() : new ItemStack(Material.AIR);
        }));
    }
}
