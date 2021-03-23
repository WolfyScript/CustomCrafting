package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Settings extends CCWindow {

    static List<String> availableLangs = new ArrayList<>();

    public Settings(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "settings", 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ToggleButton<>("lockdown", new ButtonState<>("lockdown.disabled", Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            if (ChatUtils.checkPerm(player, "customcrafting.cmd.lockdown")) {
                guiHandler.close();
                api.getChat().sendMessages(player, "&cAre you sure you want to enable LockDown mode?", "&c&lThis will disable all the custom recipes!");
                api.getChat().sendActionMessage(player, new ClickData("&7[&aYES&7]", (wolfyUtilities, player1) -> {
                    customCrafting.getConfigHandler().getConfig().setLockDown(true);
                    wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster();
                }, true), new ClickData("&7 -- ", null), new ClickData("&7[&cNO&7]", (wolfyUtilities, player1) -> wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
            }
            return true;
        }), new ButtonState<>("lockdown.enabled", Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            if (ChatUtils.checkPerm(player, "customcrafting.cmd.lockdown")) {
                guiHandler.close();
                api.getChat().sendMessages(player, "&cAre you sure you want to disable LockDown mode?", "&c&lThis will enable all the custom recipes!");
                api.getChat().sendActionMessage(player, new ClickData("&7[&aYES&7]", (wolfyUtilities, player1) -> {
                    customCrafting.getConfigHandler().getConfig().setLockDown(false);
                    wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster();
                }, true), new ClickData("&7 -- ", null), new ClickData("&7[&cNO&7]", (wolfyUtilities, player1) -> wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
            }
            return true;
        })));

        registerButton(new ToggleButton<>("darkMode", new ButtonState<>("darkMode.disabled", Material.WHITE_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            PlayerUtil.getStore(player).setDarkMode(true);
            return true;
        }), new ButtonState<>("darkMode.enabled", Material.BLACK_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            PlayerUtil.getStore(player).setDarkMode(false);
            return true;
        })));

        registerButton(new ToggleButton<>("pretty_printing", false, new ButtonState<>("pretty_printing.disabled", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setPrettyPrinting(true);
            return true;
        }), new ButtonState<>("pretty_printing.enabled", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setPrettyPrinting(false);
            return true;
        })));

        registerButton(new ToggleButton<>("advanced_workbench", false, new ButtonState<>("advanced_workbench.disabled", Material.CRAFTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(true);
            return true;
        }), new ButtonState<>("advanced_workbench.enabled", Material.CRAFTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(false);
            return true;
        })));

        registerButton(new ActionButton<>("language", new ButtonState<>("language", Material.BOOKSHELF, (cache, guiHandler, player, inventory, slot, event) -> {
            int index = availableLangs.indexOf(customCrafting.getConfigHandler().getConfig().getLanguage());
            int nextIndex = index;
            if (event instanceof InventoryClickEvent) {
                if (((InventoryClickEvent) event).isLeftClick() && !((InventoryClickEvent) event).isShiftClick()) {
                    nextIndex = (index + 1 < availableLangs.size()) ? index + 1 : 0;
                } else if (((InventoryClickEvent) event).isRightClick() && !((InventoryClickEvent) event).isShiftClick()) {
                    nextIndex = index - 1 >= 0 ? index - 1 : availableLangs.size() - 1;
                } else if (((InventoryClickEvent) event).isShiftClick()) {
                    if (ChatUtils.checkPerm(player, "customcrafting.cmd.reload")) {
                        api.getChat().sendMessage(player, "&eReloading Inventories and Languages!");
                        customCrafting.getApi().getLanguageAPI().unregisterLanguages();
                        customCrafting.getConfigHandler().getConfig().save();
                        customCrafting.getConfigHandler().loadLang();
                        customCrafting.getApi().getInventoryAPI().reset();
                        api.getChat().sendMessage(player, "&aReload complete! Reloaded GUIs and languages");
                        guiHandler.close();
                        return true;
                    }
                    return true;
                }
            }
            customCrafting.getConfigHandler().getConfig().setLanguage(availableLangs.get(nextIndex));
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, b) -> {
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
        })));

        registerButton(new ToggleButton<>("debug", false, new ButtonState<>("debug.disabled", Material.REDSTONE, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().set("debug", true);
            return true;
        }), new ButtonState<>("debug.enabled", Material.REDSTONE, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().set("debug", false);
            return true;
        })));

        registerButton(new ToggleButton<>("creator.reset_after_save", false, new ButtonState<>("creator.reset_after_save.disabled", PlayerHeadUtils.getViaURL("e551153a1519357b6241ab1ddcae831dff080079c0b2960797c702dd92266835"), (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setResetCreatorAfterSave(true);
            return true;
        }), new ButtonState<>("creator.reset_after_save.enabled", PlayerHeadUtils.getViaURL("c65cb185c641cbe74e70bce6e6a1ed90a180ec1a42034d5c4aed57af560fc83a"), (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setResetCreatorAfterSave(false);
            return true;
        })));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        availableLangs.clear();
        File langFolder = new File(customCrafting.getDataFolder() + File.separator + "lang");
        String[] filenames = langFolder.list((dir, name) -> name.endsWith(".json"));
        if (filenames != null) {
            availableLangs.addAll(Arrays.stream(filenames).map(s -> s.replace(".json", "")).distinct().collect(Collectors.toList()));
        }
        Player player = event.getPlayer();

        ((ToggleButton<CCCache>) getButton("lockdown")).setState(event.getGuiHandler(), !customCrafting.getConfigHandler().getConfig().isLockedDown());
        ((ToggleButton<CCCache>) getButton("darkMode")).setState(event.getGuiHandler(), !PlayerUtil.getStore(event.getPlayer()).isDarkMode());
        ((ToggleButton<CCCache>) getButton("pretty_printing")).setState(event.getGuiHandler(), !customCrafting.getConfigHandler().getConfig().isPrettyPrinting());
        ((ToggleButton<CCCache>) getButton("advanced_workbench")).setState(event.getGuiHandler(), !customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled());
        ((ToggleButton<CCCache>) getButton("debug")).setState(event.getGuiHandler(), !api.hasDebuggingMode());
        ((ToggleButton<CCCache>) getButton("creator.reset_after_save")).setState(event.getGuiHandler(), !customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave());

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
