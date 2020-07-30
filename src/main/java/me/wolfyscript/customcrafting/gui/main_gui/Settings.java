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
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Settings extends ExtendedGuiWindow {

    static List<String> availableLangs = new ArrayList<>();

    public Settings(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("settings", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ToggleButton("lockdown", new ButtonState("lockdown.disabled", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (ChatUtils.checkPerm(player, "customcrafting.cmd.lockdown")) {
                guiHandler.close();
                api.sendPlayerMessage(player, "&cAre you sure you want to enable LockDown mode?");
                api.sendPlayerMessage(player, "&c&lThis will disable all the custom recipes!");
                api.sendActionMessage(player, new ClickData("&7[&aYES&7]", (wolfyUtilities, player1) -> {
                    customCrafting.getConfigHandler().getConfig().setLockDown(true);
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
                    customCrafting.getConfigHandler().getConfig().setLockDown(false);
                    wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster();
                }, true), new ClickData("&7 -- ", null), new ClickData("&7[&cNO&7]", (wolfyUtilities, player1) -> wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
            }
            return true;
        })));

        registerButton(new ToggleButton("darkMode", new ButtonState("darkMode.disabled", Material.WHITE_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerStatistics(player).setDarkMode(true);
            return true;
        }), new ButtonState("darkMode.enabled", Material.BLACK_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerStatistics(player).setDarkMode(false);
            return true;
        })));

        registerButton(new ToggleButton("pretty_printing", false, new ButtonState("pretty_printing.disabled", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            customCrafting.getConfigHandler().getConfig().setPrettyPrinting(true);
            return true;
        }), new ButtonState("pretty_printing.enabled", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            customCrafting.getConfigHandler().getConfig().setPrettyPrinting(false);
            return true;
        })));

        registerButton(new ToggleButton("advanced_workbench", false, new ButtonState("advanced_workbench.disabled", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            customCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(true);
            return true;
        }), new ButtonState("advanced_workbench.enabled", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            customCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(false);
            return true;
        })));

        registerButton(new ActionButton("language", new ButtonState("language", Material.BOOKSHELF, new ButtonActionRender() {

            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent event) {
                int index = availableLangs.indexOf(customCrafting.getConfigHandler().getConfig().getLanguage());
                int nextIndex = index;
                if (event.isLeftClick() && !event.isShiftClick()) {
                    nextIndex = (index + 1 < availableLangs.size()) ? index + 1 : 0;
                } else if (event.isRightClick() && !event.isShiftClick()) {
                    nextIndex = index - 1 >= 0 ? index - 1 : availableLangs.size() - 1;
                } else if (event.isShiftClick()) {
                    if (ChatUtils.checkPerm(player, "customcrafting.cmd.reload")) {
                        api.sendPlayerMessage(player, "&eReloading Inventories and Languages!");
                        CustomCrafting.getApi().getInventoryAPI().reset();
                        CustomCrafting.getApi().getLanguageAPI().unregisterLanguages();
                        customCrafting.getConfigHandler().getConfig().save();
                        customCrafting.getRecipeHandler().onSave();
                        customCrafting.getConfigHandler().load();
                        InventoryHandler invHandler = new InventoryHandler(customCrafting);
                        invHandler.init();
                        api.sendPlayerMessage(player, "&aReload complete! Reloaded GUIs and languages");
                        return true;
                    }
                    return true;
                }
                customCrafting.getConfigHandler().getConfig().setlanguage(availableLangs.get(nextIndex));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                int index = availableLangs.indexOf(customCrafting.getConfigHandler().getConfig().getLanguage());
                List<String> displayLangs = new ArrayList<>();
                displayLangs.addAll(availableLangs.subList(index, availableLangs.size()));
                displayLangs.addAll(availableLangs.subList(0, index));
                for (int i = 0; i < 5; i++) {
                    if (i < displayLangs.size()) {
                        hashMap.put("%lang" + i + "%", displayLangs.get(i));
                    } else {
                        hashMap.put("%lang" + i + "%", "");
                    }
                }
                return itemStack;
            }
        })));

        registerButton(new ToggleButton("debug", false, new ButtonState("debug.disabled", Material.REDSTONE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            customCrafting.getConfigHandler().getConfig().set("debug", true);
            return true;
        }), new ButtonState("debug.enabled", Material.REDSTONE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            customCrafting.getConfigHandler().getConfig().set("debug", false);
            return true;
        })));

        registerButton(new ToggleButton("creator.reset_after_save", false, new ButtonState("creator.reset_after_save.disabled", PlayerHeadUtils.getViaURL("e551153a1519357b6241ab1ddcae831dff080079c0b2960797c702dd92266835"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            customCrafting.getConfigHandler().getConfig().setResetCreatorAfterSave(true);
            return true;
        }), new ButtonState("creator.reset_after_save.enabled", PlayerHeadUtils.getViaURL("c65cb185c641cbe74e70bce6e6a1ed90a180ec1a42034d5c4aed57af560fc83a"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            customCrafting.getConfigHandler().getConfig().setResetCreatorAfterSave(false);
            return true;
        })));

        registerButton(new ToggleButton("knowledgebook.workbench_filter_button", false, new ButtonState("knowledgebook.workbench_filter_button.disabled", PlayerHeadUtils.getViaURL("e551153a1519357b6241ab1ddcae831dff080079c0b2960797c702dd92266835"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            customCrafting.getConfigHandler().getConfig().setWorkbenchFilter(true);
            return true;
        }), new ButtonState("knowledgebook.workbench_filter_button.enabled", PlayerHeadUtils.getViaURL("c65cb185c641cbe74e70bce6e6a1ed90a180ec1a42034d5c4aed57af560fc83a"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            customCrafting.getConfigHandler().getConfig().setWorkbenchFilter(false);
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            availableLangs.clear();
            File langFolder = new File(customCrafting.getDataFolder() + File.separator + "lang");
            String[] filenames = langFolder.list((dir, name) -> name.endsWith(".json"));
            availableLangs.add("de_DE");
            for (String filename : filenames) {
                String name = filename.replace(".json", "");
                if (!availableLangs.contains(name)) {
                    availableLangs.add(name);
                }
            }
            Player player = event.getPlayer();

            ((ToggleButton) event.getGuiWindow().getButton("lockdown")).setState(event.getGuiHandler(), !customCrafting.getConfigHandler().getConfig().isLockedDown());
            ((ToggleButton) event.getGuiWindow().getButton("darkMode")).setState(event.getGuiHandler(), !CustomCrafting.getPlayerStatistics(event.getPlayer()).getDarkMode());
            ((ToggleButton) event.getGuiWindow().getButton("pretty_printing")).setState(event.getGuiHandler(), !customCrafting.getConfigHandler().getConfig().isPrettyPrinting());
            ((ToggleButton) event.getGuiWindow().getButton("advanced_workbench")).setState(event.getGuiHandler(), !customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled());
            ((ToggleButton) event.getGuiWindow().getButton("debug")).setState(event.getGuiHandler(), !api.hasDebuggingMode());
            ((ToggleButton) event.getGuiWindow().getButton("creator.reset_after_save")).setState(event.getGuiHandler(), !customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave());
            ((ToggleButton) event.getGuiWindow().getButton("knowledgebook.workbench_filter_button")).setState(event.getGuiHandler(), !customCrafting.getConfigHandler().getConfig().workbenchFilter());

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
                event.setButton(14, "creator.reset_after_save");
                event.setButton(15, "knowledgebook.workbench_filter_button");
            }
            if (ChatUtils.checkPerm(player, "customcrafting.cmd.debug")) {
                event.setButton(35, "debug");
            }
        }
    }
}
