package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.handlers.InventoryHandler;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Settings extends ExtendedGuiWindow {

    static List<String> availableLangs = new ArrayList<>();

    public Settings(InventoryAPI inventoryAPI) {
        super("settings", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        registerButton(new ToggleButton("lockdown", new ButtonState("lockdown.disabled", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
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

        registerButton(new ToggleButton("darkMode", new ButtonState("darkMode.disabled", Material.WHITE_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).setDarkMode(true);
            return true;
        }), new ButtonState("darkMode.enabled", Material.BLACK_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).setDarkMode(false);
            return true;
        })));

        registerButton(new ToggleButton("pretty_printing", new ButtonState("pretty_printing.disabled", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getConfigHandler().getConfig().setPrettyPrinting(false);
            return true;
        }), new ButtonState("pretty_printing.enabled", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getConfigHandler().getConfig().setPrettyPrinting(true);
            return true;
        })));

        registerButton(new ToggleButton("advanced_workbench", new ButtonState("advanced_workbench.enabled", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(true);
            return true;
        }), new ButtonState("advanced_workbench.disabled", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(false);
            return true;
        })));

        registerButton(new ActionButton("language", new ButtonState("language", Material.BOOKSHELF, new ButtonActionRender() {

            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent event) {
                int index = availableLangs.indexOf(CustomCrafting.getConfigHandler().getConfig().getLanguage());
                int nextIndex = index;
                if(event.isLeftClick() && !event.isShiftClick()){
                    nextIndex = (index + 1 < availableLangs.size()) ? index + 1 : 0;
                }else if(event.isRightClick() && !event.isShiftClick()){
                    nextIndex = index - 1 >= 0 ? index - 1 : availableLangs.size()-1;
                }else if(event.isShiftClick()){
                    if (ChatUtils.checkPerm(player, "customcrafting.cmd.reload")) {
                        api.sendPlayerMessage(player, "&eReloading Inventories and Languages!");
                        CustomCrafting.getApi().getInventoryAPI().reset();
                        CustomCrafting.getApi().getLanguageAPI().unregisterLanguages();
                        CustomCrafting.getConfigHandler().getConfig().save();
                        CustomCrafting.getRecipeHandler().onSave();
                        CustomCrafting.getConfigHandler().load();
                        InventoryHandler invHandler = new InventoryHandler(api);
                        invHandler.init();
                        api.sendPlayerMessage(player, "&aReload complete! Reloaded GUIs and languages");
                        return true;
                    }
                    return true;
                }
                CustomCrafting.getConfigHandler().getConfig().setlanguage(availableLangs.get(nextIndex));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                int index = availableLangs.indexOf(CustomCrafting.getConfigHandler().getConfig().getLanguage());
                List<String> displayLangs = new ArrayList<>();
                displayLangs.addAll(availableLangs.subList(index, availableLangs.size()));
                displayLangs.addAll(availableLangs.subList(0, index));
                for(int i = 0; i < 5; i++){
                    if(i < displayLangs.size()){
                        hashMap.put("%lang"+i+"%", displayLangs.get(i));
                    }else{
                        hashMap.put("%lang"+i+"%", "");
                    }
                }
                return itemStack;
            }
        })));

        registerButton(new ToggleButton("debug", new ButtonState("debug.disabled", Material.REDSTONE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getConfigHandler().getConfig().set("debug", false);
            return true;
        }), new ButtonState("debug.enabled", Material.REDSTONE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getConfigHandler().getConfig().set("debug", true);
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            availableLangs.clear();
            File langFolder = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "lang");
            String[] filenames = langFolder.list((dir, name) -> {
                if(name.endsWith(".json")){
                    return true;
                }
                return false;
            });
            availableLangs.add("de_DE");
            for(String filename : filenames){
                String name = filename.replace(".json", "");
                if(!availableLangs.contains(name)){
                    availableLangs.add(name);
                }
            }

            Player player = event.getPlayer();

            ((ToggleButton) event.getGuiWindow().getButton("lockdown")).setState(event.getGuiHandler(), !CustomCrafting.getConfigHandler().getConfig().isLockedDown());
            ((ToggleButton) event.getGuiWindow().getButton("darkMode")).setState(event.getGuiHandler(), !CustomCrafting.getPlayerCache(event.getPlayer()).getDarkMode());
            ((ToggleButton) event.getGuiWindow().getButton("pretty_printing")).setState(event.getGuiHandler(), CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
            ((ToggleButton) event.getGuiWindow().getButton("advanced_workbench")).setState(event.getGuiHandler(), !CustomCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled());
            ((ToggleButton) event.getGuiWindow().getButton("debug")).setState(event.getGuiHandler(), api.hasDebuggingMode());

            event.setButton(0, "none", "back");

            if (ChatUtils.checkPerm(player, "customcrafting.cmd.lockdown")) {
                event.setButton(9, "lockdown");
            }
            if (ChatUtils.checkPerm(player, "customcrafting.cmd.darkmode")) {
                event.setButton(10, "darkMode");
            }
            if (ChatUtils.checkPerm(player, "customcrafting.cmd.settings")) {
                event.setButton(11, "pretty_printing");
                event.setButton(12, "advanced_workbench");
                event.setButton(13, "language");
            }


            if (ChatUtils.checkPerm(player, "customcrafting.cmd.debug")) {
                event.setButton(35, "debug");
            }
        }
    }
}
