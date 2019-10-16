package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.gui.crafting.*;
import me.wolfyscript.customcrafting.gui.main_gui.MainMenu;
import me.wolfyscript.customcrafting.gui.main_gui.VariantMenu;
import me.wolfyscript.customcrafting.gui.main_gui.items.ItemCreator;
import me.wolfyscript.customcrafting.gui.main_gui.items.ItemEditor;
import me.wolfyscript.customcrafting.gui.main_gui.list.RecipesList;
import me.wolfyscript.customcrafting.gui.recipe_creator.ConditionsMenu;
import me.wolfyscript.customcrafting.gui.main_gui.recipe.RecipeEditor;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.AnvilCreator;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.CookingCreator;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.StonecutterCreator;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.WorkbenchCreator;
import me.wolfyscript.customcrafting.gui.recipebook.RecipeBook;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiCluster;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import org.bukkit.Material;

public class InventoryHandler {

    private WolfyUtilities api;
    private InventoryAPI invAPI;

    public InventoryHandler(WolfyUtilities api) {
        this.api = api;
        this.invAPI = api.getInventoryAPI();
    }

    public void init() {
        api.sendConsoleMessage("$msg.startup.inventories$");
        registerInvs();
        invAPI.registerButton("none", new DummyButton("glass_gray", new ButtonState("none", "background", Material.GRAY_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_black", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_red", new ButtonState("none", "background", Material.RED_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_white", new ButtonState("none", "background", Material.WHITE_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_green", new ButtonState("none", "background", Material.GREEN_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_purple", new ButtonState("none", "background", Material.PURPLE_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_pink", new ButtonState("none", "background", Material.PINK_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new ToggleButton("gui_help", true, new ButtonState("gui_help_off", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.setHelpEnabled(true);
            return true;
        }), new ButtonState("gui_help_on", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.setHelpEnabled(false);
            return true;
        })));
        invAPI.registerButton("none", new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
    }

    private void registerInvs() {
        //Main Cluster
        GuiCluster mainCluster = invAPI.getOrRegisterGuiCluster("none");
        mainCluster.registerGuiWindow(new MainMenu(invAPI));
        mainCluster.registerGuiWindow(new ItemEditor(invAPI));
        mainCluster.registerGuiWindow(new ItemCreator(invAPI));
        mainCluster.registerGuiWindow(new RecipeEditor(invAPI));
        mainCluster.registerGuiWindow(new RecipesList(invAPI));
        mainCluster.registerGuiWindow(new VariantMenu(invAPI));
        mainCluster.setMainmenu("main_menu");

        GuiCluster recipeCreator = invAPI.getOrRegisterGuiCluster("recipe_creator");
        recipeCreator.registerGuiWindow(new AnvilCreator(invAPI));
        recipeCreator.registerGuiWindow(new CookingCreator(invAPI));
        recipeCreator.registerGuiWindow(new StonecutterCreator(invAPI));
        recipeCreator.registerGuiWindow(new WorkbenchCreator(invAPI));
        recipeCreator.registerGuiWindow(new ConditionsMenu(invAPI));

        recipeCreator.registerButton(new ActionButton("conditions", new ButtonState("conditions", Material.CYAN_CONCRETE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("conditions");
            return true;
        })), invAPI.getWolfyUtilities());

        GuiCluster recipeBook = invAPI.getOrRegisterGuiCluster("recipe_book");
        recipeBook.registerGuiWindow(new RecipeBook(invAPI));
        recipeBook.registerGuiWindow(new me.wolfyscript.customcrafting.gui.recipebook.MainMenu(invAPI));
        recipeBook.setMainmenu("main_menu");

        GuiCluster craftingCluster = new GuiCluster();
        invAPI.registerCustomGuiCluster("crafting", craftingCluster);
        craftingCluster.registerGuiWindow(new CraftingWindow3(invAPI));
        craftingCluster.registerGuiWindow(new CraftingWindow4(invAPI));
        craftingCluster.registerGuiWindow(new CraftingWindow5(invAPI));
        craftingCluster.registerGuiWindow(new CraftingWindow6(invAPI));
        craftingCluster.setMainmenu("crafting_3");
        craftingCluster.registerButton(new ActionButton("knowledge_book", new ButtonState("crafting","knowledge_book", Material.KNOWLEDGE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {

            return true;
        })), invAPI.getWolfyUtilities());

    }


}
