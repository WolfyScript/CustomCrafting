package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.item_creator.ClusterItemCreator;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class MenuItemEditor extends CCWindow {

    public MenuItemEditor(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "item_editor", 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>(ClusterMain.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            if (cache.getSetting().equals(Setting.RECIPE_CREATOR)) {
                cache.getItems().setRecipeItem(false);
                cache.setApplyItem(null);
                guiHandler.openPreviousWindow();
            } else {
                guiHandler.openCluster(ClusterMain.KEY);
            }
            return true;
        })));
        registerButton(new ActionButton<>("create_item", Material.ITEM_FRAME, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow(ClusterItemCreator.MAIN_MENU);
            return true;
        }));
        registerButton(new ActionButton<>(ClusterMain.ITEM_LIST.getKey(), new ButtonState<>(ClusterMain.ITEM_LIST, Material.BOOKSHELF, (cache, guiHandler, player, guiInventory, i, event) -> {
            guiHandler.openWindow(ClusterMain.ITEM_LIST);
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");
        event.setButton(21, ClusterMain.ITEM_LIST.getKey());
        event.setButton(23, "create_item");
    }
}
