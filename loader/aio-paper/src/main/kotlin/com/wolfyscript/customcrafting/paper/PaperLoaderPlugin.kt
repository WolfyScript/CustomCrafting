package com.wolfyscript.customcrafting.paper

import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.sentry.initSentry
import com.wolfyscript.scafall.Scafall
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.loader.ScafallLoader
import io.sentry.Sentry
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PaperLoaderPlugin : JavaPlugin() {

    val boostrap = ScafallLoader.loadObject(
        CustomCraftingBoostrap::class.java,
        classLoader,
        CustomCraftingBoostrap.PATH_TO_INTERNAL_BOOTSTRAP
    )
    private val customCrafting: CustomCraftingPaper = boostrap.loadModule {
        CustomCraftingPaper(this, slF4JLogger)
    }

    init {
        initSentry()

        Sentry.configureScope { scope ->
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