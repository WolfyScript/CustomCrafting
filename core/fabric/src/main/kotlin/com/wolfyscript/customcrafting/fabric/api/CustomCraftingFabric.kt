package com.wolfyscript.customcrafting.fabric.api

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.fabric.CustomCraftingServerFabric
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
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