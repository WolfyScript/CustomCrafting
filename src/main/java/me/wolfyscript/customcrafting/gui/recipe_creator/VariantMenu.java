package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.VariantContainerButton;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.Material;

import java.util.List;

public class VariantMenu extends ExtendedGuiWindow {

    public VariantMenu(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("variants", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 45; i++) {
            registerButton(new VariantContainerButton(i, customCrafting));
        }
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            int resultSlot = 9;
            switch (cache.getRecipeType()) {
                case ELITE_WORKBENCH:
                    resultSlot = 36;
                case WORKBENCH:
                    if (cache.getVariantsData().getSlot() == resultSlot) {
                        List<CustomItem> items = cache.getVariantsData().getVariants();
                        items.removeIf(item -> item == null || item.getItemStack().getType().equals(Material.AIR));
                        cache.getWorkbenchRecipe().setResult(items);
                    } else {
                        cache.getWorkbenchRecipe().setIngredients(cache.getVariantsData().getSlot(), cache.getVariantsData().getVariants());
                    }
                    break;
                case ANVIL:
                    List<CustomItem> items = cache.getVariantsData().getVariants();
                    items.removeIf(item -> item == null || item.getItemStack().getType().equals(Material.AIR));
                    cache.getAnvilRecipe().setInput(cache.getVariantsData().getSlot(), cache.getVariantsData().getVariants());
                    break;
                case STONECUTTER:
                    if (cache.getVariantsData().getSlot() != 1) {
                        cache.getStonecutterRecipe().setSource(cache.getVariantsData().getVariants());
                    }
                    break;
                case FURNACE:
                case SMOKER:
                case BLAST_FURNACE:
                case CAMPFIRE:
                    if (cache.getVariantsData().getSlot() != 1) {
                        cache.getCookingRecipe().setSource(cache.getVariantsData().getVariants());
                    } else {
                        cache.getCookingRecipe().setResult(cache.getVariantsData().getVariants());
                    }
                    break;
                case CAULDRON:
                    List<CustomItem> variants = cache.getVariantsData().getVariants();
                    variants.removeIf(item -> item == null || item.getItemStack().getType().equals(Material.AIR));
                    if (cache.getVariantsData().getSlot() == 0) {
                        cache.getCauldronRecipe().setIngredients(variants);
                    } else {
                        cache.getCauldronRecipe().setResult(variants);
                    }

            }
            guiHandler.openPreviousInv();
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        event.setButton(0, "back");
        for (int i = 0; i < 45; i++) {
            event.setButton(9 + i, "variant_container_" + i);
        }
    }
}
