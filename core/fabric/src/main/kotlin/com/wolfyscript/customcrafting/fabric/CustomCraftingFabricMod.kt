package com.wolfyscript.customcrafting.fabric

import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.core.commands.CCCommands
import com.wolfyscript.customcrafting.core.data.DataManager
import com.wolfyscript.customcrafting.core.sentry.setupSentry
import com.wolfyscript.customcrafting.core.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.loader.ScafallLoader
import com.wolfyscript.scafall.platform.PlatformType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.io.File

class CustomCraftingFabricMod : ModInitializer {

    private val boostrap = ScafallLoader.loadObject(
        CustomCraftingBoostrap::class.java,
        javaClass.classLoader,
        CustomCraftingBoostrap.PATH_TO_INTERNAL_BOOTSTRAP
    )
    private val logger = LoggerFactory.getLogger(javaClass)
    private lateinit var customCrafting: CustomCraftingFabric

    init {
        setupSentry(
            FabricLoader.getInstance().rawGameVersion,
            PlatformType.FABRIC,
            File(FabricLoader.getInstance().configDir.toFile(), "${Key.CUSTOMCRAFTING_NAMESPACE}/${DataManager.DATA_PATH}"),
        )

        ScafallProvider.whenReady { // Load order isn't deterministic, so need to make sure scafall is available!
            customCrafting = boostrap.loadModule {
                CustomCraftingFabric(logger)
            }
        }
    }

    override fun onInitialize() {

        ServerLifecycleEvents.SERVER_STARTING.register { mcServer ->
            logger.info("CustomCraftingFabricMod server starting")

            ScafallProvider.whenReady {
                it.onServerAvailable {
                    logger.info("[${CUSTOMCRAFTING_NAMESPACE}] Loading server...")

                    customCrafting.initServer(mcServer)
                    customCrafting.configurationManager.load()

                    customCrafting.server?.onLoad()
                }
            }
        }

        ServerLifecycleEvents.SERVER_STARTED.register {
            logger.info("CustomCraftingFabricMod server startet")
        }

        ServerLifecycleEvents.SERVER_STOPPED.register {

            customCrafting.server?.onUnload()
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, env ->
            if (env.includeDedicated) {
                logger.info("Registering CustomCraftingFabricMod commands")
                CCCommands.registerCommands(dispatcher)
            }
        }

    }

}