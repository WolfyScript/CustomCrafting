package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.elite_crafting.EliteCraftingCluster;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;

class MenuMain extends CCWindow {

    private static final String BACK_BOTTOM = "back_bottom";

    MenuMain(ClusterRecipeBook cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterRecipeBook.MAIN_MENU.getKey(), 27, customCrafting);
    }

    @Override
    public void onInit() {
        var dataHandler = customCrafting.getDataHandler();
        var categories = dataHandler.getCategories();

        for (String categoryId : categories.getSortedCategories()) {
            registerButton(new ButtonCategoryMain(categoryId, customCrafting));
        }
        registerButton(new ActionButton<>(BACK_BOTTOM, new ButtonState<>(ClusterMain.BACK_BOTTOM, Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                if (cache.getKnowledgeBook().hasEliteCraftingTable()) {
                    guiHandler.openCluster(EliteCraftingCluster.KEY);
                } else {
                    guiHandler.close();
                }
                cache.getKnowledgeBook().setEliteCraftingTable(null);
            });
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        event.setButton(8, data.getLightBackground());

        var dataHandler = customCrafting.getDataHandler();
        var categories = dataHandler.getCategories();
        var sorted = categories.getSortedCategories();

        for (int i = 0; i < sorted.size() && i < getSize(); i++) {
            event.setButton(i, "main_category." + sorted.get(i));
        }
        event.setButton(22, BACK_BOTTOM);
    }
}
