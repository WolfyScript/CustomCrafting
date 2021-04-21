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
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class MainCluster extends CCCluster {

    public static final String KEY = "none";

    //Button keys
    public static final NamespacedKey BACK = new NamespacedKey(KEY, "back");
    public static final NamespacedKey BACK_BOTTOM = new NamespacedKey(KEY, "back_bottom");
    public static final NamespacedKey GUI_HELP = new NamespacedKey(KEY, "gui_help");
    public static final NamespacedKey GLASS_GRAY = new NamespacedKey(KEY, "glass_gray");
    public static final NamespacedKey GLASS_WHITE = new NamespacedKey(KEY, "glass_white");
    public static final NamespacedKey GLASS_BLACK = new NamespacedKey(KEY, "glass_black");
    public static final NamespacedKey GLASS_RED = new NamespacedKey(KEY, "glass_red");
    public static final NamespacedKey GLASS_GREEN = new NamespacedKey(KEY, "glass_green");
    public static final NamespacedKey GLASS_PURPLE = new NamespacedKey(KEY, "glass_purple");
    public static final NamespacedKey GLASS_PINK = new NamespacedKey(KEY, "glass_pink");
    public static final NamespacedKey PATREON = new NamespacedKey(KEY, "patreon");
    public static final NamespacedKey INSTAGRAM = new NamespacedKey(KEY, "instagram");
    public static final NamespacedKey YOUTUBE = new NamespacedKey(KEY, "youtube");
    public static final NamespacedKey DISCORD = new NamespacedKey(KEY, "discord");

    //Language keys
    public static final NamespacedKey BACKGROUND = new NamespacedKey(KEY, "background");

    //Both Button and Window keys
    public static final NamespacedKey RECIPE_LIST = new NamespacedKey(KEY, "recipe_list");

    //Window keys
    public static final NamespacedKey ITEM_EDITOR = new NamespacedKey(KEY, "item_editor");
    public static final NamespacedKey ITEM_LIST = new NamespacedKey(KEY, "item_list");
    //Message keys

    public MainCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new DummyButton<>(GLASS_GRAY.getKey(), new ButtonState<>(BACKGROUND.getKey(), Material.GRAY_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>(GLASS_BLACK.getKey(), new ButtonState<>(BACKGROUND.getKey(), Material.BLACK_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>(GLASS_RED.getKey(), new ButtonState<>(BACKGROUND.getKey(), Material.RED_STAINED_GLASS_PANE)));

        registerButton(new DummyButton<>(GLASS_WHITE.getKey(), new ButtonState<>(BACKGROUND.getKey(), Material.WHITE_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>(GLASS_GREEN.getKey(), new ButtonState<>(BACKGROUND.getKey(), Material.GREEN_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>(GLASS_PURPLE.getKey(), new ButtonState<>(BACKGROUND.getKey(), Material.PURPLE_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>(GLASS_PINK.getKey(), new ButtonState<>(BACKGROUND.getKey(), Material.PINK_STAINED_GLASS_PANE)));

        registerButton(new ToggleButton<>(GUI_HELP.getKey(), true, new ButtonState<>("gui_help_off", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.setHelpEnabled(true);
            return true;
        }), new ButtonState<>("gui_help_on", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.setHelpEnabled(false);
            return true;
        })));
        registerButton(new ActionButton<>(BACK.getKey(), PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        }));
        registerButton(new ActionButton<>(BACK_BOTTOM.getKey(), Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        }));

        registerButton(new ActionButton<>(PATREON.getKey(), PlayerHeadUtils.getViaURL("5693b66a595f78af3f51f4efa4c13375b1b958e6f4c507a47c4fe565cc275"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow("patrons_menu");
            return true;
        }));
        registerButton(new ActionButton<>(INSTAGRAM.getKey(), PlayerHeadUtils.getViaURL("ac88d6163fabe7c5e62450eb37a074e2e2c88611c998536dbd8429faa0819453"), (cache, guiHandler, player, inventory, slot, event) -> {
            wolfyUtilities.getChat().sendActionMessage(player, new ClickData("&7[&3Click here to go to Instagram&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.instagram.com/wolfyscript/")));
            return true;
        }));
        registerButton(new ActionButton<>(YOUTUBE.getKey(), PlayerHeadUtils.getViaURL("b4353fd0f86314353876586075b9bdf0c484aab0331b872df11bd564fcb029ed"), (cache, guiHandler, player, inventory, slot, event) -> {
            wolfyUtilities.getChat().sendActionMessage(player, new ClickData("&7[&3Click here to go to YouTube&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/channel/UCTlqRLm4PxZuAI4nVN4X74g")));
            return true;
        }));
        registerButton(new ActionButton<>(DISCORD.getKey(), PlayerHeadUtils.getViaURL("4d42337be0bdca2128097f1c5bb1109e5c633c17926af5fb6fc20000011aeb53"), (cache, guiHandler, player, inventory, slot, event) -> {
            wolfyUtilities.getChat().sendActionMessage(player, new ClickData("&7[&3Click here to join Discord&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/qGhDTSr")));
            return true;
        }));
        registerButton(new ActionButton<>(RECIPE_LIST.getKey(), Material.WRITTEN_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().setSetting(Setting.RECIPE_LIST);
            guiHandler.openWindow(RECIPE_LIST.getKey());
            return true;
        }));
        registerButton(new ActionButton<>(ITEM_LIST.getKey(), Material.BOOKSHELF, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow(ITEM_LIST.getKey());
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
