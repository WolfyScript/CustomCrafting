package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;

public abstract class ExtendedGuiWindow extends GuiWindow {

    protected CustomCrafting customCrafting;
    protected WolfyUtilities api = CustomCrafting.getApi();

    public ExtendedGuiWindow(String namespace, InventoryAPI inventoryAPI, int size, CustomCrafting customCrafting) {
        super(namespace, inventoryAPI, size);
        this.customCrafting = customCrafting;
    }

    public CustomCrafting getCustomCrafting() {
        return customCrafting;
    }

    @Override
    public void onUpdateAsync(GuiUpdate update) {
        PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(update.getPlayer());
        if (!getNamespace().startsWith("crafting_grid")) {
            if (getSize() > 9) {
                for (int i = 0; i < 9; i++) {
                    update.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
                }
                for (int i = 9; i < getSize() - 9; i++) {
                    update.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                }
                for (int i = getSize() - 9; i < getSize(); i++) {
                    update.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
                }
                update.setButton(8, "none", "gui_help");
            } else {
                for (int i = 0; i < 9; i++) {
                    update.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                }
            }
        }
    }
}
