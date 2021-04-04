package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class EditorMain extends CCWindow {

    public EditorMain(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "editor_main", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new ActionButton<>("filters", Material.COMPASS, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().setFilters(true);
            guiHandler.openWindow("categories");
            return true;
        }));
        registerButton(new ActionButton<>("categories", Material.CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().setFilters(false);
            guiHandler.openWindow("categories");
            return true;
        }));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        update.setButton(20, "categories");
        update.setButton(24, "filters");


    }
}
