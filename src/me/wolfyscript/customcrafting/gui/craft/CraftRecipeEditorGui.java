package me.wolfyscript.customcrafting.gui.craft;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.PlayerSettings;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class CraftRecipeEditorGui extends GuiWindow {


    public CraftRecipeEditorGui(InventoryAPI inventoryAPI) {
        super("craft_editor_main", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("save", Material.WRITABLE_BOOK);
        createItem("permissions", WolfyUtilities.getSkullByValue(""));
        createItem("adv_workbench", WolfyUtilities.getSkullByValue(""));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event){
        if(event.verify(this)){
            PlayerSettings playerSettings = CustomCrafting.getPlayerSettings(event.getPlayer());
            if(!playerSettings.getCachedCraftIngredients().isEmpty()){
                int slot;
                for(int i = 0; i < 9 ; i++){
                    slot = 19 + i + (i/3)*6;
                    event.setItem(slot, playerSettings.getCachedCraftIngredients().get(i));
                }
            }
            event.setItem(33, playerSettings.getCachedResult());
            event.setItem(53, "save");
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        return super.onAction(guiAction);
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        return super.onClick(guiClick);
    }
}
