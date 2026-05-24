package com.wolfyscript.customcrafting.paper

import com.wolfyscript.customcrafting.core.CustomCraftingCommon
import com.wolfyscript.customcrafting.core.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.core.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.customcrafting.spigotlike.CustomCraftingServerSpigotLike
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.core.ModIdentifier
import com.wolfyscript.scafall.identifier.Key
import io.sentry.Sentry
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.slf4j.Logger

class CustomCraftingPaper(val plugin: Plugin, override val logger: Logger) : CustomCraftingCommon() {

    override val configurationManager = ConfigurationManagerImpl(this, plugin.dataFolder)
    override val identifier: ModIdentifier = ScafallProvider.get().getModIdentifier(Key.CUSTOMCRAFTING_NAMESPACE)!!

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