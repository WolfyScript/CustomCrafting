package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonContainerIngredientItems;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

public class IngredientMenu extends CCWindow {

    public IngredientMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "ingredients", 54, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 36; i++) {
            registerButton(new ButtonContainerIngredientItems(i));
        }

        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {


            guiHandler.openPreviousWindow();
            return true;
        })));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        for (int i = 0; i < 36; i++) {
            update.setButton(9 + i, "item_container_" + i);
        }


    }
}
