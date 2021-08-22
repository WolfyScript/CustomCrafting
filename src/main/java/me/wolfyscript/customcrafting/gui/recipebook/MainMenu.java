package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.EliteCraftingCluster;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.RecipeBookCluster;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.MainCategoryButton;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class MainMenu extends CCWindow {

    private static final String BACK_BOTTOM = "back_bottom";

    public MainMenu(RecipeBookCluster cluster, CustomCrafting customCrafting) {
        super(cluster, RecipeBookCluster.MAIN_MENU.getKey(), 27, customCrafting);
    }

    @Override
    public void onInit() {
        var dataHandler = customCrafting.getDataHandler();
        var categories = dataHandler.getCategories();

        for (String categoryId : categories.getSortedCategories()) {
            registerButton(new MainCategoryButton(categoryId, customCrafting));
        }
        registerButton(new ActionButton<>(BACK_BOTTOM, new ButtonState<>(MainCluster.BACK_BOTTOM, Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
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

        int slot = 0;
        for (String categoryId : categories.getSortedCategories()) {
            event.setButton(slot, "main_category." + categoryId);
            slot++;
        }

        event.setButton(22, BACK_BOTTOM);
    }
}
