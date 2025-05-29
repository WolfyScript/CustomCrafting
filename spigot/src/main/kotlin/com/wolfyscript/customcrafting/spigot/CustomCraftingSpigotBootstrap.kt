package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCraftingProvider
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CustomCraftingSpigotBootstrap : JavaPlugin() {

    val slf4jLogger: Logger = LoggerFactory.getLogger(logger.name)

    private val customCrafting = CustomCraftingSpigot(this, slf4jLogger)

    init {
        CustomCraftingProvider.register(customCrafting)
    }

    override fun onLoad() {

    }

    override fun onEnable() {

    }

    override fun onDisable() {

    }

}