package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

public class TagSettings extends CCWindow {

    public TagSettings(GuiCluster<CCCache> guiCluster, CustomCrafting customCrafting) {
        super(guiCluster, "tag_settings", 54, customCrafting);
    }

    @Override
    public void onInit() {

        registerButton(new ActionButton<>("add_tag.list", Material.NAME_TAG, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.openWindow("tag_list");
            return true;
        }));

    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);

        update.setButton(49, "add_tag.list");


    }
}
