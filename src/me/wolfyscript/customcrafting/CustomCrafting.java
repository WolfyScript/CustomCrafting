package me.wolfyscript.customcrafting;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomCrafting extends JavaPlugin {

    private static Plugin instance;
    private static WolfyUtilities api;

    public void onEnable() {
        instance = this;
        api = new WolfyUtilities(instance);
        api.setCHAT_PREFIX("§7[§6CC§7] ");
        api.setCONSOLE_PREFIX("§7[§3CC§7] ");



    }

    public void onDisable() {

    }



    public static Plugin getInst() {
        return instance;
    }

    public static WolfyUtilities getApi() {
        return api;
    }
}
