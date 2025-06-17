package com.wolfyscript.customcrafting.spigot.loader

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.scafall.loader.InnerJarClassloader
import com.wolfyscript.scafall.loader.ScafallLoader
import com.wolfyscript.scafall.loader.module.Module
import org.bukkit.plugin.java.JavaPlugin

class SpigotLoaderPlugin : JavaPlugin() {

    private val module: Module<CustomCrafting>

    init {
        val overlayLoader = InnerJarClassloader.create("customcrafting-spigot-innerjar", classLoader, classLoader, "customcrafting-spigot.innerjar")
        val boostrap = ScafallLoader.loadObject(
            CustomCraftingBoostrap::class.java,
            overlayLoader,
            CustomCraftingBoostrap.PATH_TO_INTERNAL_BOOTSTRAP
        )
        module = boostrap.loadModuleFromInnerJar("com.wolfyscript.customcrafting.spigot.CustomCraftingSpigotBootstrap", JavaPlugin::class.java, this)
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