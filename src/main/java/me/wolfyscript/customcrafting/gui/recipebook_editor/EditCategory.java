package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeBookEditorCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class EditCategory extends EditCategorySetting {

    private static final String AUTO = "auto";

    public EditCategory(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "category", customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        registerButton(new ToggleButton<>(AUTO, new ButtonState<>("auto.enabled", Material.COMMAND_BLOCK, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            cache.getRecipeBookEditor().getCategory().setAuto(false);
            return true;
        }), new ButtonState<>("auto.disabled", Material.PLAYER_HEAD, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            cache.getRecipeBookEditor().getCategory().setAuto(true);
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        ((ToggleButton<CCCache>) getButton(AUTO)).setState(update.getGuiHandler(), update.getGuiHandler().getCustomCache().getRecipeBookEditor().getCategory().isAuto());

        update.setButton(22, AUTO);
        if (!update.getGuiHandler().getCustomCache().getRecipeBookEditor().getCategory().isAuto()) {
            update.setButton(29, RecipeBookEditorCluster.RECIPES);
            update.setButton(33, RecipeBookEditorCluster.NAMESPACES);
            update.setButton(40, RecipeBookEditorCluster.GROUPS);
        }
    }
}
