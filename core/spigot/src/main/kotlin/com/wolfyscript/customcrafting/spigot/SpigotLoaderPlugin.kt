package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.core.data.DataManager
import com.wolfyscript.customcrafting.core.sentry.setupSentry
import com.wolfyscript.customcrafting.core.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.loader.ScafallLoader
import com.wolfyscript.scafall.platform.PlatformType
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.LoggerFactory
import java.io.File

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
            PlatformType.PAPER,
            File(dataFolder, "${Key.CUSTOMCRAFTING_NAMESPACE}/${DataManager.DATA_PATH}")
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