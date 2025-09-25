package com.wolfyscript.customcrafting.paper

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.sentry.initSentry
import com.wolfyscript.scafall.loader.ScafallLoader
import com.wolfyscript.scafall.loader.module.Module
import io.sentry.Sentry
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PaperLoaderPlugin : JavaPlugin() {

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
            CustomCraftingPaper(this, slF4JLogger)
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