package com.wolfyscript.customcrafting.fabric

import com.wolfyscript.customcrafting.core.CustomCraftingCommon
import com.wolfyscript.customcrafting.core.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.core.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.core.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.identifier.Key
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import java.io.File

class CustomCraftingFabric(
    override val logger: Logger,
) : CustomCraftingCommon() {

    override val configurationManager: ConfigurationManager

    init {
        val configRoot = FabricLoader.getInstance().configDir.toFile()
        val ccDir = File(configRoot, Key.CUSTOMCRAFTING_NAMESPACE)
        configurationManager = ConfigurationManagerImpl(this, ccDir)
    }

    fun initServer(minecraftServer: MinecraftServer) {
        server = CustomCraftingServerFabric(this, minecraftServer)
    }

    override fun onInit() {
        logger.info("Initializing ${CUSTOMCRAFTING_NAMESPACE}...")
    }

}