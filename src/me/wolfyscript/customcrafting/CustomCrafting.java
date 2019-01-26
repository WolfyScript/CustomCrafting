package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.events.Events;
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.InventoryHandler;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomCrafting extends JavaPlugin {

    private static Plugin instance;
    private static WolfyUtilities api;
    private static ConfigHandler configHandler;
    private static InventoryHandler invHandler;
    private static RecipeHandler recipeHandler;

    public void onEnable() {
        instance = this;
        api = new WolfyUtilities(instance);
        api.setCHAT_PREFIX("§7[§6CC§7] ");
        api.setCONSOLE_PREFIX("§7[§3CC§7] ");
        configHandler = new ConfigHandler(api);
        invHandler = new InventoryHandler(api);
        recipeHandler = new RecipeHandler(api);

        getServer().getPluginManager().registerEvents(new Events(api), this);

        configHandler.load();

        api.sendConsoleMessage("  _____        __             _____         _____  _          ");
        api.sendConsoleMessage(" / ___/_ _____/ /____  __ _  / ___/______ _/ _/ /_(_)__  ___ _");
        api.sendConsoleMessage("/ /__/ // (_-< __/ _ \\/  ' \\/ /__/ __/ _ `/ _/ __/ / _ \\/ _ `/");
        api.sendConsoleMessage("\\___/\\_,_/___|__/\\___/_/_/_/\\___/_/  \\_,_/_/ \\__/_/_//_/\\_, / ");
        api.sendConsoleMessage("                                                       /___/ v"+instance.getDescription().getVersion());

        invHandler.init();
        recipeHandler.loadConfigs();
        recipeHandler.loadRecipes();


    }


    public void onDisable() {

    }



    public static Plugin getInst() {
        return instance;
    }

    public static WolfyUtilities getApi() {
        return api;
    }

    public static RecipeHandler getRecipeHandler() {
        return recipeHandler;
    }
}
