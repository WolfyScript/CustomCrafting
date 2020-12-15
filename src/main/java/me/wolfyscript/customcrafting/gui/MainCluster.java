package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.lists.CustomItemList;
import me.wolfyscript.customcrafting.gui.lists.RecipesList;
import me.wolfyscript.customcrafting.gui.main_gui.*;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.chat.ClickEvent;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;

public class MainCluster extends CCCluster {

    public MainCluster(InventoryAPI<TestCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, "none", customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new DummyButton("glass_gray", new ButtonState("none", "background", Material.GRAY_STAINED_GLASS_PANE, 8999)));
        registerButton(new DummyButton("glass_black", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 8999)));
        registerButton(new DummyButton("glass_red", new ButtonState("none", "background", Material.RED_STAINED_GLASS_PANE, 8999)));

        registerButton(new DummyButton("glass_white", new ButtonState("none", "background", Material.WHITE_STAINED_GLASS_PANE, 8999)));

        registerButton(new DummyButton("glass_green", new ButtonState("none", "background", Material.GREEN_STAINED_GLASS_PANE, 8999)));
        registerButton(new DummyButton("glass_purple", new ButtonState("none", "background", Material.PURPLE_STAINED_GLASS_PANE, 8999)));
        registerButton(new DummyButton("glass_pink", new ButtonState("none", "background", Material.PINK_STAINED_GLASS_PANE, 8999)));

        registerButton(new ToggleButton("gui_help", true, new ButtonState("gui_help_off", new ItemBuilder(Material.PLAYER_HEAD).setPlayerHeadValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19").create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.setHelpEnabled(true);
            return true;
        }), new ButtonState("gui_help_on", new ItemBuilder(Material.PLAYER_HEAD).setPlayerHeadValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19").create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.setHelpEnabled(false);
            return true;
        })));
        registerButton(new ActionButton("back", new ButtonState("none", "back", new ItemBuilder(Material.PLAYER_HEAD).setPlayerHeadValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0=").create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));

        registerGuiWindow(new MainMenu(this, customCrafting));
        registerGuiWindow(new ItemEditor(this, customCrafting));
        registerGuiWindow(new CustomItemList(this, customCrafting));
        registerGuiWindow(new RecipeEditor(this, customCrafting));
        registerGuiWindow(new RecipesList(this, customCrafting));
        registerGuiWindow(new Settings(this, customCrafting));
        registerGuiWindow(new PatronsMenu(this, customCrafting));
        setEntry(new NamespacedKey("none", "main_menu"));

        registerButton(new ActionButton("patreon", new ButtonState("main_menu", "patreon", PlayerHeadUtils.getViaURL("5693b66a595f78af3f51f4efa4c13375b1b958e6f4c507a47c4fe565cc275"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("patrons_menu");
            return true;
        })));
        registerButton(new ActionButton("instagram", new ButtonState("main_menu", "instagram", PlayerHeadUtils.getViaURL("ac88d6163fabe7c5e62450eb37a074e2e2c88611c998536dbd8429faa0819453"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            wolfyUtilities.getChat().sendActionMessage(player, new ClickData("&7[&3Click here to go to Instagram&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.instagram.com/_gunnar.h_/")));
            return true;
        })));
        registerButton(new ActionButton("youtube", new ButtonState("main_menu", "youtube", PlayerHeadUtils.getViaURL("b4353fd0f86314353876586075b9bdf0c484aab0331b872df11bd564fcb029ed"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            wolfyUtilities.getChat().sendActionMessage(player, new ClickData("&7[&3Click here to go to YouTube&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/channel/UCTlqRLm4PxZuAI4nVN4X74g")));
            return true;
        })));
        registerButton(new ActionButton("discord", new ButtonState("main_menu", "discord", PlayerHeadUtils.getViaURL("4d42337be0bdca2128097f1c5bb1109e5c633c17926af5fb6fc20000011aeb53"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            wolfyUtilities.getChat().sendActionMessage(player, new ClickData("&7[&3Click here to join Discord&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/qGhDTSr")));
            return true;
        })));
        registerButton(new ActionButton("recipe_list", new ButtonState("main_menu", "recipe_list", Material.WRITTEN_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.RECIPE_LIST);
            guiHandler.changeToInv("recipe_list");
            return true;
        })));
        registerButton(new ActionButton("item_list", new ButtonState("main_menu", "item_list", Material.BOOKSHELF, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("item_list");
            return true;
        })));
    }
}
