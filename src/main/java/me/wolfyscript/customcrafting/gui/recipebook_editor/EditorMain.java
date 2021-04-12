package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

import java.io.IOException;

public class EditorMain extends CCWindow {

    private static final String SAVE = "save";
    private static final String CANCEL = "cancel";
    private static final String FILTERS = "filters";
    private static final String CATEGORIES = "categories";

    public EditorMain(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "editor_main", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>(CANCEL, Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().loadRecipeBookConfig();
            guiHandler.openCluster("none");
            return true;
        }));
        registerButton(new ActionButton<>(SAVE, Material.WRITTEN_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            try {
                customCrafting.getConfigHandler().save();
                api.getChat().sendKey(player, "recipe_book_editor", "save.success");
            } catch (IOException e) {
                e.printStackTrace();
            }
            guiHandler.openCluster("none");
            return true;
        }));
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
        update.setButton(0, PlayerUtil.getStore(update.getPlayer()).getLightBackground());
        update.setButton(20, CATEGORIES);
        update.setButton(24, FILTERS);

        update.setButton(39, CANCEL);
        update.setButton(41, SAVE);
    }
}
