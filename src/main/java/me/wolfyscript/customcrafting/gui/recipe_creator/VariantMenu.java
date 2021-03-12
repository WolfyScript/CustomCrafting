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
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            int resultSlot = 9;
            List<CustomItem> variants = cache.getVariantsData().getVariants();
            switch (cache.getRecipeType().getType()) {
                case ELITE_WORKBENCH:
                case WORKBENCH:
                    cache.getCraftingRecipe().setResult(variants);
                    break;
                case ANVIL:
                    cache.getAnvilRecipe().setResult(variants);
                    break;
                case STONECUTTER:
                    break;
                case FURNACE:
                case SMOKER:
                case BLAST_FURNACE:
                case CAMPFIRE:
                    cache.getCookingRecipe().setResult(variants);
                    break;
                case CAULDRON:
                    if (cache.getVariantsData().getSlot() == 0) {
                        cache.getCauldronRecipe().setIngredients(variants);
                    } else {
                        cache.getCauldronRecipe().setResult(variants);
                    }
                    break;
                case BREWING_STAND:
                    if (cache.getVariantsData().getSlot() == 1) {
                        cache.getBrewingRecipe().setAllowedItems(variants);
                    } else {
                        cache.getBrewingRecipe().setResult(variants);
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
