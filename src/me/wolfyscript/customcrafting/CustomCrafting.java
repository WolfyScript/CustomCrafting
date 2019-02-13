package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.commands.CommandCC;
import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.FurnaceConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import me.wolfyscript.customcrafting.data.Workbenches;
import me.wolfyscript.customcrafting.events.Events;
import me.wolfyscript.customcrafting.gui.PlayerCache;
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.InventoryHandler;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomCrafting extends JavaPlugin {

    private static Plugin instance;
    private static List<PlayerCache> playerCacheList = new ArrayList<>();
    private static WolfyUtilities api;
    private static ConfigHandler configHandler;
    private static InventoryHandler invHandler;
    private static RecipeHandler recipeHandler;
    private static Workbenches workbenches = null;

    public void onEnable() {
        instance = this;
        api = new WolfyUtilities(instance);
        api.setCHAT_PREFIX("§7[§6CC§7] ");
        api.setCONSOLE_PREFIX("§7[§3CC§7] ");

        System.out.println("  _____        __             _____         _____  _          ");
        System.out.println(" / ___/_ _____/ /____  __ _  / ___/______ _/ _/ /_(_)__  ___ _");
        System.out.println("/ /__/ // (_-< __/ _ \\/  ' \\/ /__/ __/ _ `/ _/ __/ / _ \\/ _ `/");
        System.out.println("\\___/\\_,_/___|__/\\___/_/_/_/\\___/_/  \\_,_/_/ \\__/_/_//_/\\_, / ");
        System.out.println("                                                       /___/ v" + instance.getDescription().getVersion());
        System.out.println(" ");
        System.out.println("This is a technical Test! It's not for use on production servers!");
        System.out.println("It's incomplete, unstable, contains bugs and doesn't represent the final Plugin!");
        System.out.println("--------------------------------------------------------------------------------");


        File mainConfig = new File(getDataFolder(), "Main-Config.yml");
        if (mainConfig.exists()) {
            System.out.println("Found old CustomCrafting data! renaming folder...");
            if (getDataFolder().renameTo(new File(getDataFolder().getParentFile(), "CustomCrafting_old"))) {
                System.out.println("Renamed to CustomCrafting_old!");

                //System.out.println("Restart server to load the new Data!");
                //Bukkit.getPluginManager().disablePlugin(this);
            }
        }

        configHandler = new ConfigHandler(api);
        invHandler = new InventoryHandler(api);
        recipeHandler = new RecipeHandler(api);

        configHandler.load();

        getServer().getPluginManager().registerEvents(new Events(api), this);
        getServer().getPluginCommand("cc").setExecutor(new CommandCC());

        invHandler.init();

        workbenches = new Workbenches(api);

        recipeHandler.loadConfigs();


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

    public static boolean hasPlayerSettings(Player player) {
        for (PlayerCache playerCache : playerCacheList) {
            if (playerCache.getUuid().equals(player.getUniqueId()))
                return true;
        }
        return false;
    }

    public static PlayerCache getPlayerCache(Player player) {
        for (PlayerCache playerCache : playerCacheList) {
            if (playerCache.getUuid().equals(player.getUniqueId()))
                return playerCache;
        }
        PlayerCache playerCache = new PlayerCache(player.getUniqueId());
        playerCacheList.add(playerCache);
        return playerCache;
    }
}
