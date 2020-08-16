package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.VariantsData;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class VariantContainerButton extends ItemInputButton {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getVariantsData().putVariant(items.getVariantSlot(), customItem);

    public VariantContainerButton(int variantSlot, CustomCrafting customCrafting) {
        super("variant_container_" + variantSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
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
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                VariantsData variantsData = ((TestCache) guiHandler.getCustomCache()).getVariantsData();
                return variantsData.getVariants() != null && variantsData.getVariants().size() > variantSlot ? variantsData.getVariants().get(variantSlot).getIDItem() : new ItemStack(Material.AIR);
            }
        }));
    }
}
