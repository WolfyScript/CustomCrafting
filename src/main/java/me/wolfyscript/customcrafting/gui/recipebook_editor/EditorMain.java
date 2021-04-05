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

    private static final String BACK = "back";
    private static final String FILTERS = "filters";
    private static final String CATEGORIES = "categories";

    public EditorMain(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "editor_main", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>(BACK, new ButtonState<>("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new ActionButton<>(FILTERS, Material.COMPASS, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().setFilters(true);
            guiHandler.openWindow(FILTERS);
            return true;
        }));
        registerButton(new ActionButton<>(CATEGORIES, Material.CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().setFilters(false);
            guiHandler.openWindow(CATEGORIES);
            return true;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        update.setButton(20, CATEGORIES);
        update.setButton(24, FILTERS);
    }
}
