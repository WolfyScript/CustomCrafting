package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.core.CustomCraftingCommon
import com.wolfyscript.customcrafting.core.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.spigotlike.CustomCraftingServerSpigotLike
import com.wolfyscript.scafall.core.ModIdentifier
import io.sentry.Sentry
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.slf4j.Logger

class CustomCraftingSpigot(val plugin: Plugin, override val logger: Logger) : CustomCraftingCommon() {

    override val configurationManager = ConfigurationManagerImpl(this, plugin.dataFolder)
    override val identifier: ModIdentifier = object : ModIdentifier {
        override val id: String = "customcrafting"
    }

    override fun onInit() {
        Sentry.configureScope { scope ->
            scope.setContexts(
                "bukkit.plugins",
                Bukkit.getPluginManager().plugins.associate { plugin -> plugin.name to plugin.description.version })
        }

        configurationManager.load()
    }

    fun initServer(bukkitServer: Server) {
        server = CustomCraftingServerSpigot(CustomCraftingServerSpigotLike(this, plugin))
    }

}