package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.VariantContainerButton;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

import java.util.List;

public class VariantMenu extends CCWindow {

    public VariantMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "variants", 54, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 45; i++) {
            registerButton(new VariantContainerButton(i));
        }
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (cache, guiHandler, player, inventory, slot, event) -> {
            int resultSlot = 9;
            List<CustomItem> variants = cache.getVariantsData().getVariants();
            switch (cache.getRecipeType().getType()) {
                case ELITE_WORKBENCH:
                    resultSlot = 36;
                case WORKBENCH:
                    if (cache.getVariantsData().getSlot() == resultSlot) {
                        cache.getCraftingRecipe().setResult(variants);
                    } else {
                        cache.getCraftingRecipe().setIngredients(cache.getVariantsData().getSlot(), variants);
                    }
                    break;
                case ANVIL:
                    cache.getAnvilRecipe().setInput(cache.getVariantsData().getSlot(), variants);
                    break;
                case STONECUTTER:
                    if (cache.getVariantsData().getSlot() != 1) {
                        cache.getStonecutterRecipe().setSource(variants);
                    }
                    break;
                case FURNACE:
                case SMOKER:
                case BLAST_FURNACE:
                case CAMPFIRE:
                    if (cache.getVariantsData().getSlot() != 1) {
                        cache.getCookingRecipe().setSource(variants);
                    } else {
                        cache.getCookingRecipe().setResult(variants);
                    }
                    break;
                case CAULDRON:
                    if (cache.getVariantsData().getSlot() == 0) {
                        cache.getCauldronRecipe().setIngredients(variants);
                    } else {
                        cache.getCauldronRecipe().setResult(variants);
                    }
                    break;
                case BREWING_STAND:
                    switch (cache.getVariantsData().getSlot()) {
                        case 0:
                            cache.getBrewingRecipe().setIngredients(variants);
                            break;
                        case 1:
                            cache.getBrewingRecipe().setAllowedItems(variants);
                            break;
                        case 2:
                            cache.getBrewingRecipe().setResult(variants);
                            break;
                    }
            }
            guiHandler.openPreviousWindow();
            return true;
        })));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");
        for (int i = 0; i < 45; i++) {
            event.setButton(9 + i, "variant_container_" + i);
        }
    }
}
