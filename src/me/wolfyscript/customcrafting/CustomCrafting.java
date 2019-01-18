package me.wolfyscript.customcrafting;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomCrafting extends JavaPlugin {

    private static Plugin instance;

    public void onEnable() {
        instance = this;

    }

    public void onDisable() {

    }



    public static Plugin getInst() {
        return instance;
    }
}
