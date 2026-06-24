package com.wolfyscript.customcrafting.paper

import com.wolfyscript.customcrafting.core.CustomCraftingCommon
import com.wolfyscript.customcrafting.core.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.core.data.DataManager
import com.wolfyscript.customcrafting.spigotlike.CustomCraftingServerSpigotLike
import io.sentry.Sentry
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.slf4j.Logger

class CustomCraftingPaper(val plugin: Plugin, override val logger: Logger) : CustomCraftingCommon() {

    override val configurationManager = ConfigurationManagerImpl(this, plugin.dataFolder)
    override val dataManager: DataManager = DataManager.createNewForDir(this, plugin.dataFolder)

    override fun onInit() {
        Sentry.configureScope { scope ->
            scope.setContexts(
                "bukkit.plugins",
                Bukkit.getPluginManager().plugins.associate { plugin -> plugin.name to plugin.pluginMeta.version })
        }

        configurationManager.load()
    }

    fun initServer(bukkitServer: Server) {
        server = CustomCraftingServerPaper(CustomCraftingServerSpigotLike(this, plugin))
    }

}