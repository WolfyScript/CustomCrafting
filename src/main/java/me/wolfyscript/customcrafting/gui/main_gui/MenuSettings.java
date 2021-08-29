package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuSettings extends CCWindow {

    static final List<String> availableLangs = new ArrayList<>();

    private static final String DARK_MODE = "darkMode";
    private static final String PRETTY_PRINTING = "pretty_printing";
    private static final String ADVANCED_CRAFTING_TABLE = "advanced_workbench";
    private static final String DEBUG = "debug";

    public MenuSettings(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "settings", 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ButtonSettingsLockdown(api, customCrafting));
        registerButton(new ButtonSettingsLanguage(availableLangs, api, customCrafting));
        registerButton(new ToggleButton<>(DARK_MODE, (ccCache, guiHandler, player, guiInventory, i) -> !PlayerUtil.getStore(player).isDarkMode(), new ButtonState<>(DARK_MODE + ".disabled", Material.WHITE_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            PlayerUtil.getStore(player).setDarkMode(true);
            return true;
        }), new ButtonState<>(DARK_MODE + ".enabled", Material.BLACK_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            PlayerUtil.getStore(player).setDarkMode(false);
            return true;
        })));
        registerButton(new ToggleButton<>(PRETTY_PRINTING, (ccCache, guiHandler, player, guiInventory, i) -> !customCrafting.getConfigHandler().getConfig().isPrettyPrinting(), new ButtonState<>(PRETTY_PRINTING + ".disabled", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setPrettyPrinting(true);
            customCrafting.getConfigHandler().getConfig().save();
            return true;
        }), new ButtonState<>(PRETTY_PRINTING + ".enabled", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setPrettyPrinting(false);
            customCrafting.getConfigHandler().getConfig().save();
            return true;
        })));
        registerButton(new ToggleButton<>(ADVANCED_CRAFTING_TABLE, (ccCache, guiHandler, player, guiInventory, i) -> !customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled(), new ButtonState<>("advanced_workbench.disabled", Material.CRAFTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(true);
            customCrafting.getConfigHandler().getConfig().save();
            return true;
        }), new ButtonState<>("advanced_workbench.enabled", Material.CRAFTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(false);
            customCrafting.getConfigHandler().getConfig().save();
            return true;
        })));
        registerButton(new ToggleButton<>(DEBUG, (ccCache, guiHandler, player, guiInventory, i) -> !api.hasDebuggingMode(), new ButtonState<>("debug.disabled", Material.REDSTONE, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().set("debug", true);
            customCrafting.getConfigHandler().getConfig().save();
            return true;
        }), new ButtonState<>("debug.enabled", Material.REDSTONE, (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().set("debug", false);
            customCrafting.getConfigHandler().getConfig().save();
            return true;
        })));
        registerButton(new ToggleButton<>("creator.reset_after_save", (ccCache, guiHandler, player, guiInventory, i) -> !customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave(), new ButtonState<>("creator.reset_after_save.disabled", PlayerHeadUtils.getViaURL("e551153a1519357b6241ab1ddcae831dff080079c0b2960797c702dd92266835"), (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setResetCreatorAfterSave(true);
            customCrafting.getConfigHandler().getConfig().save();
            return true;
        }), new ButtonState<>("creator.reset_after_save.enabled", PlayerHeadUtils.getViaURL("c65cb185c641cbe74e70bce6e6a1ed90a180ec1a42034d5c4aed57af560fc83a"), (cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().getConfig().setResetCreatorAfterSave(false);
            customCrafting.getConfigHandler().getConfig().save();
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        availableLangs.clear();
        File langFolder = new File(customCrafting.getDataFolder() + File.separator + "lang");
        String[] filenames = langFolder.list((dir, name) -> name.endsWith(".json"));
        if (filenames != null) {
            availableLangs.addAll(Arrays.stream(filenames).map(s -> s.replace(".json", "")).distinct().toList());
        }
        Player player = event.getPlayer();
        event.setButton(0, ClusterMain.BACK);
        if (ChatUtils.checkPerm(player, "customcrafting.cmd.lockdown")) {
            event.setButton(9, ButtonSettingsLockdown.KEY);
        }
        if (ChatUtils.checkPerm(player, "customcrafting.cmd.darkmode")) {
            event.setButton(10, DARK_MODE);
        }
        if (ChatUtils.checkPerm(player, "customcrafting.cmd.settings")) {
            event.setButton(11, PRETTY_PRINTING);
            event.setButton(12, ADVANCED_CRAFTING_TABLE);
            event.setButton(13, ButtonSettingsLanguage.KEY);
            event.setButton(14, "creator.reset_after_save");
            event.setButton(15, "knowledgebook.workbench_filter_button");
        }
        if (ChatUtils.checkPerm(player, "customcrafting.cmd.debug")) {
            event.setButton(35, DEBUG);
        }
    }
}
