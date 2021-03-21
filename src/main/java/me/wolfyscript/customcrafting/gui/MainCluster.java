package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.lists.CustomItemList;
import me.wolfyscript.customcrafting.gui.lists.RecipesList;
import me.wolfyscript.customcrafting.gui.main_gui.ItemEditor;
import me.wolfyscript.customcrafting.gui.main_gui.MainMenu;
import me.wolfyscript.customcrafting.gui.main_gui.PatronsMenu;
import me.wolfyscript.customcrafting.gui.main_gui.Settings;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.chat.ClickEvent;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class MainCluster extends CCCluster {

    public MainCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, "none", customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new DummyButton<>("glass_gray", new ButtonState<>("background", Material.GRAY_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>("glass_black", new ButtonState<>("background", Material.BLACK_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>("glass_red", new ButtonState<>("background", Material.RED_STAINED_GLASS_PANE)));

        registerButton(new DummyButton<>("glass_white", new ButtonState<>("background", Material.WHITE_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>("glass_green", new ButtonState<>("background", Material.GREEN_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>("glass_purple", new ButtonState<>("background", Material.PURPLE_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>("glass_pink", new ButtonState<>("background", Material.PINK_STAINED_GLASS_PANE)));

        registerButton(new ToggleButton<>("gui_help", true, new ButtonState<>("gui_help_off", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.setHelpEnabled(true);
            return true;
        }), new ButtonState<>("gui_help_on", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.setHelpEnabled(false);
            return true;
        })));
        registerButton(new ActionButton<>("back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        }));

        registerButton(new ActionButton<>("patreon", PlayerHeadUtils.getViaURL("5693b66a595f78af3f51f4efa4c13375b1b958e6f4c507a47c4fe565cc275"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow("patrons_menu");
            return true;
        }));
        registerButton(new ActionButton<>("instagram", PlayerHeadUtils.getViaURL("ac88d6163fabe7c5e62450eb37a074e2e2c88611c998536dbd8429faa0819453"), (cache, guiHandler, player, inventory, slot, event) -> {
            wolfyUtilities.getChat().sendActionMessage(player, new ClickData("&7[&3Click here to go to Instagram&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.instagram.com/wolfyscript/")));
            return true;
        }));
        registerButton(new ActionButton<>("youtube", PlayerHeadUtils.getViaURL("b4353fd0f86314353876586075b9bdf0c484aab0331b872df11bd564fcb029ed"), (cache, guiHandler, player, inventory, slot, event) -> {
            wolfyUtilities.getChat().sendActionMessage(player, new ClickData("&7[&3Click here to go to YouTube&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/channel/UCTlqRLm4PxZuAI4nVN4X74g")));
            return true;
        }));
        registerButton(new ActionButton<>("discord", PlayerHeadUtils.getViaURL("4d42337be0bdca2128097f1c5bb1109e5c633c17926af5fb6fc20000011aeb53"), (cache, guiHandler, player, inventory, slot, event) -> {
            wolfyUtilities.getChat().sendActionMessage(player, new ClickData("&7[&3Click here to join Discord&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/qGhDTSr")));
            return true;
        }));
        registerButton(new ActionButton<>("recipe_list", Material.WRITTEN_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().setSetting(Setting.RECIPE_LIST);
            guiHandler.openWindow("recipe_list");
            return true;
        }));
        registerButton(new ActionButton<>("item_list", Material.BOOKSHELF, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow("item_list");
            return true;
        }));

        registerGuiWindow(new MainMenu(this, customCrafting));
        registerGuiWindow(new ItemEditor(this, customCrafting));
        registerGuiWindow(new CustomItemList(this, customCrafting));
        registerGuiWindow(new RecipesList(this, customCrafting));
        registerGuiWindow(new Settings(this, customCrafting));
        registerGuiWindow(new PatronsMenu(this, customCrafting));
    }
}
