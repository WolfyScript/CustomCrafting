package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.core.sentry.setupSentry
import com.wolfyscript.scafall.loader.ScafallLoader
import com.wolfyscript.scafall.platform.PlatformType
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.LoggerFactory

class SpigotLoaderPlugin : JavaPlugin() {

    val boostrap = ScafallLoader.loadObject(
        CustomCraftingBoostrap::class.java,
        classLoader,
        CustomCraftingBoostrap.PATH_TO_INTERNAL_BOOTSTRAP
    )
    private val customCrafting: CustomCraftingSpigot = boostrap.loadModule {
        CustomCraftingSpigot(this, LoggerFactory.getLogger(logger.name))
    }

    init {
        setupSentry(
            MinecraftServer.getServer().serverVersion,
            PlatformType.PAPER
        ) { scope ->
            scope.setTag("bukkit.version", Bukkit.getVersion())
        }
    }

    override fun onLoad() {
        customCrafting.configurationManager.load()

        customCrafting.initServer(Bukkit.getServer())
    }

    override fun onEnable() {
        customCrafting.server?.onLoad()
    }

    override fun onDisable() {
        customCrafting.server?.onUnload()
    }

}