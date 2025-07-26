package com.wolfyscript.customcrafting.spigot.loader

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.spigot.CustomCraftingSpigot
import com.wolfyscript.scafall.loader.InnerJarClassloader
import com.wolfyscript.scafall.loader.ScafallLoader
import com.wolfyscript.scafall.loader.module.Module
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.LoggerFactory

class SpigotLoaderPlugin : JavaPlugin() {

    private val module: Module<CustomCrafting>

    init {
        val boostrap = ScafallLoader.loadObject(
            CustomCraftingBoostrap::class.java,
            classLoader,
            CustomCraftingBoostrap.Companion.PATH_TO_INTERNAL_BOOTSTRAP
        )
        module = boostrap.loadModule {
            CustomCraftingSpigot(classLoader, this, LoggerFactory.getLogger(logger.name))
        }
    }

    override fun onLoad() {
        module.onLoad()
    }

    override fun onEnable() {
        module.onEnable()
    }

    override fun onDisable() {
        module.onUnload()
    }

}