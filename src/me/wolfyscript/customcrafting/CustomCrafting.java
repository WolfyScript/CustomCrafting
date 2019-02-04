package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.commands.CommandCC;
import me.wolfyscript.customcrafting.data.Workbenches;
import me.wolfyscript.customcrafting.events.Events;
import me.wolfyscript.customcrafting.gui.PlayerSettings;
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.InventoryHandler;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CustomCrafting extends JavaPlugin {

    private static Plugin instance;
    private static List<PlayerSettings> playerSettingsList = new ArrayList<>();
    private static WolfyUtilities api;
    private static ConfigHandler configHandler;
    private static InventoryHandler invHandler;
    private static RecipeHandler recipeHandler;
    private static Workbenches workbenches;

    public void onEnable() {
        instance = this;
        api = new WolfyUtilities(instance);
        api.setCHAT_PREFIX("§7[§6CC§7] ");
        api.setCONSOLE_PREFIX("§7[§3CC§7] ");
        configHandler = new ConfigHandler(api);
        invHandler = new InventoryHandler(api);
        recipeHandler = new RecipeHandler(api);

        getServer().getPluginManager().registerEvents(new Events(api), this);
        getServer().getPluginCommand("cc").setExecutor(new CommandCC());

        configHandler.load();

        api.sendConsoleMessage("  _____        __             _____         _____  _          ");
        api.sendConsoleMessage(" / ___/_ _____/ /____  __ _  / ___/______ _/ _/ /_(_)__  ___ _");
        api.sendConsoleMessage("/ /__/ // (_-< __/ _ \\/  ' \\/ /__/ __/ _ `/ _/ __/ / _ \\/ _ `/");
        api.sendConsoleMessage("\\___/\\_,_/___|__/\\___/_/_/_/\\___/_/  \\_,_/_/ \\__/_/_//_/\\_, / ");
        api.sendConsoleMessage("                                                       /___/ v"+instance.getDescription().getVersion());

        invHandler.init();

        workbenches = new Workbenches(api);

        recipeHandler.loadConfigs();
        recipeHandler.loadRecipes();


    }


    public void onDisable() {
        workbenches.endTask();
        workbenches.save();

    }

    public static ConfigHandler getConfigHandler() {
        return configHandler;
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

    public static Workbenches getWorkbenches() {
        return workbenches;
    }

    public static boolean hasPlayerSettings(Player player){
        for(PlayerSettings playerSettings : playerSettingsList){
            if(playerSettings.getUuid().equals(player.getUniqueId()))
                return true;
        }
        return false;
    }

    public static PlayerSettings getPlayerSettings(Player player){
        for(PlayerSettings playerSettings : playerSettingsList){
            if(playerSettings.getUuid().equals(player.getUniqueId()))
                return playerSettings;
        }
        PlayerSettings playerSettings = new PlayerSettings(player.getUniqueId());
        playerSettingsList.add(playerSettings);
        return playerSettings;
    }
}
