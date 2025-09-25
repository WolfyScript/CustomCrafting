package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.sentry.initSentry
import com.wolfyscript.scafall.loader.ScafallLoader
import com.wolfyscript.scafall.loader.module.Module
import io.sentry.Sentry
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.LoggerFactory

class SpigotLoaderPlugin : JavaPlugin() {

    private val module: Module<CustomCrafting>

    init {
        initSentry()

        Sentry.configureScope { scope ->
            scope.setTag("bukkit.version", Bukkit.getVersion())
        }

        val boostrap = ScafallLoader.loadObject(
            CustomCraftingBoostrap::class.java,
            classLoader,
            CustomCraftingBoostrap.PATH_TO_INTERNAL_BOOTSTRAP
        )
        module = boostrap.loadModule {
            CustomCraftingSpigot(this, LoggerFactory.getLogger(logger.name))
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