package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
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
        registerButton(new ToggleButton<>(AUTO, new ButtonState<>("auto.enabled", Material.COMMAND_BLOCK, (customCache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> true), new ButtonState<>("auto.disabled", Material.PLAYER_HEAD, (customCache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> true)));

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);

        update.setButton(31, AUTO);

    }
}
