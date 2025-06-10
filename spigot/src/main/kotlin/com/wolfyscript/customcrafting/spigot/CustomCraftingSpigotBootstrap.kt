package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CustomCraftingSpigotBootstrap(val innerJarClassloader: ClassLoader, val plugin: JavaPlugin) : CustomCraftingBoostrap.CustomCraftingModule {

    val slf4jLogger: Logger = LoggerFactory.getLogger(plugin.logger.name)

    override val bridge: CustomCraftingSpigot = CustomCraftingSpigot(this, slf4jLogger)

    override fun onLoad() {
        bridge.load()
    }

    override fun onEnable() {

    }

    override fun onUnload() {
        bridge.unload()
    }

}