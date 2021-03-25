package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonContainerItemIngredient;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class IngredientMenu extends CCWindow {

    public IngredientMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "ingredient", 54, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 36; i++) {
            registerButton(new ButtonContainerItemIngredient(i));
        }
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipe().setIngredient(cache.getIngredientData().getSlot(), cache.getIngredientData().getIngredient());
            guiHandler.openPreviousWindow();
            return true;
        })));
        registerButton(new ActionButton<>("tags", new ButtonState<>("recipe_creator", "tags", Material.NAME_TAG, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            cache.getTagSettingsCache().setRecipeItemStack(cache.getIngredientData().getIngredient());
            guiHandler.openWindow("tag_settings");
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        for (int i = 0; i < 36; i++) {
            update.setButton(9 + i, "item_container_" + i);
        }
        update.setButton(49, "tags");
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> update) {

    }
}
