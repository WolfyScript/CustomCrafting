package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.MainCategoryButton;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

public class MainMenu extends CCWindow {

    public MainMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "main_menu", 18, customCrafting);
    }

    @Override
    public void onInit() {
        DataHandler dataHandler = customCrafting.getRecipeHandler();
        Categories categories = dataHandler.getCategories();

        for (String categoryId : categories.getSortedMainCategories()) {
            registerButton(new MainCategoryButton(categoryId, customCrafting));
        }
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        event.setButton(8, "none", data.isDarkMode() ? "glass_gray" : "glass_white");

        DataHandler dataHandler = customCrafting.getRecipeHandler();
        Categories categories = dataHandler.getCategories();

        int slot = 0;
        for (String categoryId : categories.getSortedMainCategories()) {
            event.setButton(slot, "main_category." + categoryId);
            slot++;
        }
    }
}
