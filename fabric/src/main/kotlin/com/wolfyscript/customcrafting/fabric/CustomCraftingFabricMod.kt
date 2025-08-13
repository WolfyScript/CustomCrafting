package com.wolfyscript.customcrafting.fabric

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.fabric.api.CustomCraftingFabric
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.loader.ScafallLoader.loadObject
import com.wolfyscript.scafall.loader.module.Module
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.commands.Commands
import org.slf4j.LoggerFactory

class CustomCraftingFabricMod : ModInitializer {

    private val logger = LoggerFactory.getLogger(javaClass)
    private var serverModule: Module<CustomCrafting>? = null

    override fun onInitialize() {
        val boostrap = loadObject(
            CustomCraftingBoostrap::class.java,
            javaClass.classLoader,
            CustomCraftingBoostrap.PATH_TO_INTERNAL_BOOTSTRAP
        )

        ServerLifecycleEvents.SERVER_STARTING.register { mcServer ->
            logger.info("CustomCraftingFabricMod server starting")
            ScafallProvider.whenReady {
                serverModule = boostrap.loadModule {
                    CustomCraftingFabric(javaClass.classLoader, this, mcServer, logger)
                }
                serverModule?.onLoad()
            }
        }

        ServerLifecycleEvents.SERVER_STARTED.register {
            logger.info("CustomCraftingFabricMod server startet")
            serverModule?.onEnable()
        }

        ServerLifecycleEvents.SERVER_STOPPED.register {
            serverModule?.onUnload()
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, env ->
            if (env.includeDedicated) {
                if (serverModule != null) {
                    (serverModule?.bridge as? CustomCraftingCommon)?.commands?.registerCommands(dispatcher)
                }
            }
        }

    }

}