package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

public class Settings extends ExtendedGuiWindow {

    public Settings(InventoryAPI inventoryAPI) {
        super("settings", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        registerButton(new ToggleButton("lockdown", !CustomCrafting.getConfigHandler().getConfig().isLockedDown(), new ButtonState("lockdown.disabled", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (ChatUtils.checkPerm(player, "customcrafting.cmd.lockdown")) {
                guiHandler.close();
                api.sendPlayerMessage(player, "&cAre you sure you want to enable LockDown mode?");
                api.sendPlayerMessage(player, "&c&lThis will disable all the custom recipes!");
                api.sendActionMessage(player, new ClickData("&7[&aYES&7]", (wolfyUtilities, player1) -> {
                    CustomCrafting.getConfigHandler().getConfig().setLockDown(true);
                    wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster();
                }, true), new ClickData("&7 -- ", null), new ClickData("&7[&cNO&7]", (wolfyUtilities, player1) -> wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
            }
            return true;
        }), new ButtonState("lockdown.enabled", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (ChatUtils.checkPerm(player, "customcrafting.cmd.lockdown")) {
                guiHandler.close();
                api.sendPlayerMessage(player, "&cAre you sure you want to disable LockDown mode?");
                api.sendPlayerMessage(player, "&c&lThis will enable all the custom recipes!");
                api.sendActionMessage(player, new ClickData("&7[&aYES&7]", (wolfyUtilities, player1) -> {
                    CustomCrafting.getConfigHandler().getConfig().setLockDown(false);
                    wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster();
                }, true), new ClickData("&7 -- ", null), new ClickData("&7[&cNO&7]", (wolfyUtilities, player1) -> wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
            }
            return true;
        })));

        registerButton(new ToggleButton("darkMode", !CustomCrafting.getConfigHandler().getConfig().isLockedDown(), new ButtonState("darkMode.disabled", Material.WHITE_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).setDarkMode(true);
            return true;
        }), new ButtonState("darkMode.enabled", Material.BLACK_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).setDarkMode(false);
            return true;
        })));

    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            ((ToggleButton) event.getGuiWindow().getButton("lockdown")).setState(event.getGuiHandler(), !CustomCrafting.getConfigHandler().getConfig().isLockedDown());
            ((ToggleButton) event.getGuiWindow().getButton("darkMode")).setState(event.getGuiHandler(), !CustomCrafting.getPlayerCache(event.getPlayer()).getDarkMode());
            event.setButton(0, "none", "back");
            event.setButton(9, "lockdown");
            event.setButton(10, "darkMode");


        }
    }
}
