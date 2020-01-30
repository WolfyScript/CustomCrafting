package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PatronsMenu extends ExtendedGuiWindow {

    public PatronsMenu(InventoryAPI inventoryAPI) {
        super("patrons_menu", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUpdateGuis(GuiUpdateEvent event) {
        if (event.getWolfyUtilities().equals(CustomCrafting.getApi()) && event.getGuiHandler().getCurrentInv() != null && event.getGuiHandler().getCurrentInv().equals(event.getGuiWindow())) {
            PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(event.getPlayer());
            if (!event.getGuiWindow().getNamespace().startsWith("crafting_grid")) {
                if (event.getGuiHandler().getCurrentInv().getSize() > 9) {
                    for (int i = 0; i < 9; i++) {
                        event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
                    }
                    for (int i = 9; i < event.getGuiHandler().getCurrentInv().getSize() - 9; i++) {
                        event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                    }
                    for (int i = event.getGuiHandler().getCurrentInv().getSize() - 9; i < event.getGuiHandler().getCurrentInv().getSize(); i++) {
                        event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
                    }
                    event.setButton(8, "none", "gui_help");
                } else {
                    for (int i = 0; i < 9; i++) {
                        event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                    }
                }
            }
        }
    }
}
