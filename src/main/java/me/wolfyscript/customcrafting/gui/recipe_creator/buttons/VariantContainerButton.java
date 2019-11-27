package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.VariantsData;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
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

    public VariantContainerButton(int variantSlot) {
        super("variant_container_" + variantSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                PlayerCache cache = CustomCrafting.getPlayerCache(player);
                VariantsData variantsData = cache.getVariantsData();
                if (event.getClick().equals(ClickType.SHIFT_RIGHT)) {
                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                            CustomCrafting.getPlayerCache(player).getItems().setVariant(variantSlot, CustomItem.getByItemStack(inventory.getItem(slot)));
                            cache.setApplyItem(APPLY_ITEM);
                            guiHandler.changeToInv("none","item_editor");
                        }
                    });
                    return true;
                }
                Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> variantsData.putVariant(variantSlot, inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR)));
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                VariantsData variantsData = CustomCrafting.getPlayerCache(player).getVariantsData();
                if (variantsData.getVariants() != null) {
                    itemStack = variantsData.getVariants().size() > variantSlot ? variantsData.getVariants().get(variantSlot).getIDItem() : new CustomItem(Material.AIR);
                }
                return itemStack;
            }
        }));
    }
}
