package com.wolfyscript.customcrafting.paper

import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.EditorModule
import com.wolfyscript.customcrafting.sentry.initSentry
import com.wolfyscript.customcrafting.ui.UIModule
import com.wolfyscript.customcrafting.util.ModuleImpl
import com.wolfyscript.scafall.loader.ScafallLoader
import com.wolfyscript.scafall.loader.module.Module
import io.sentry.Sentry
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.full.findAnnotations

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

        CustomCraftingProvider.whenReady {
            loadModule<EditorModule>()?.onInit()
            loadModule<UIModule>()?.onInit()
        }
    }

    private inline fun <reified T: Module<*,*>> loadModule(): T? {
        return T::class.findAnnotations(ModuleImpl::class).firstOrNull()?.let {
            ScafallLoader.loadObject<T>(this.classLoader, it.implType.java.name)
        }
    }

    override fun onEnable() {
        customCrafting.server?.onLoad()
    }

    override fun onDisable() {
        customCrafting.server?.onUnload()
    }

}