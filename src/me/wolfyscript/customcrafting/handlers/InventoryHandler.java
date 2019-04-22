package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.gui.list.RecipeBook;
import me.wolfyscript.customcrafting.gui.list.RecipesList;
import me.wolfyscript.customcrafting.gui.items.ItemCreator;
import me.wolfyscript.customcrafting.gui.recipe.RecipeCreator;
import me.wolfyscript.customcrafting.gui.recipe.RecipeEditor;
import me.wolfyscript.customcrafting.gui.items.ItemEditor;
import me.wolfyscript.customcrafting.gui.MainMenu;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryHandler {

    private WolfyUtilities api;
    private InventoryAPI invAPI;

    public InventoryHandler(WolfyUtilities api){
        this.api = api;
        this.invAPI = api.getInventoryAPI();
    }

    public void init(){
        api.sendConsoleMessage("$msg.startup.inventories$");
        invAPI.registerItem("none", "glass_gray", new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        invAPI.registerItem("none", "glass_red", new ItemStack(Material.RED_STAINED_GLASS_PANE));
        invAPI.registerItem("none", "glass_green", new ItemStack(Material.GREEN_STAINED_GLASS_PANE));
        invAPI.registerItem("none", "glass_white", new ItemStack(Material.WHITE_STAINED_GLASS_PANE));

        invAPI.registerItem("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="));
        invAPI.registerItem("none", "close", new ItemStack(Material.BARRIER));
        invAPI.registerItem("none", "gui_help_on", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"));
        invAPI.registerItem("none", "gui_help_off", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"));

        registerInvs();
    }

    private void registerInvs(){
        invAPI.registerGuiWindow(new MainMenu(invAPI));

        invAPI.registerGuiWindow(new ItemEditor(invAPI));
        invAPI.registerGuiWindow(new ItemCreator(invAPI));

        invAPI.registerGuiWindow(new RecipeEditor(invAPI));
        invAPI.registerGuiWindow(new RecipeCreator(invAPI));

        invAPI.registerGuiWindow(new RecipesList(invAPI));

        invAPI.registerGuiWindow(new RecipeBook(invAPI));

        invAPI.setMainmenu("main_menu");
    }


}
