package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.gui.RecipeListGui;
import me.wolfyscript.customcrafting.gui.craft.CraftRecipeEditorGui;
import me.wolfyscript.customcrafting.gui.craft.CraftRecipeMainMenu;
import me.wolfyscript.customcrafting.gui.furnace.FurnaceRecipeMainMenu;
import me.wolfyscript.customcrafting.gui.items.ItemCreatorGui;
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
        invAPI.registerItem("none", "glass_gray", new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        invAPI.registerItem("none", "glass_red", new ItemStack(Material.RED_STAINED_GLASS_PANE));

        invAPI.registerItem("none", "minimize", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTc5YTQ2NTE4M2EzYmE2M2ZlNmFlMjcyYmMxYmYxY2QxNWYyYzIwOWViYmZjYzVjNTIxYjk1MTQ2ODJhNDMifX19"));
        invAPI.registerItem("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="));
        invAPI.registerItem("none", "close", new ItemStack(Material.BARRIER));
        invAPI.registerItem("none", "gui_help", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"));

        registerInvs();
    }

    private void registerInvs(){
        invAPI.registerGuiWindow(new MainMenu(invAPI));
        invAPI.registerGuiWindow(new ItemCreatorGui(invAPI));
        invAPI.registerGuiWindow(new FurnaceRecipeMainMenu(invAPI));
        invAPI.registerGuiWindow(new CraftRecipeMainMenu(invAPI));
        invAPI.registerGuiWindow(new CraftRecipeEditorGui(invAPI));
        invAPI.registerGuiWindow(new RecipeListGui(invAPI));


        invAPI.setMainmenu("main_menu");
    }

}
