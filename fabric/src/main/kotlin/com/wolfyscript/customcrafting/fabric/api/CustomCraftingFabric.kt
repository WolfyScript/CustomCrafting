package com.wolfyscript.customcrafting.fabric.api

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.fabric.CustomCraftingFabricMod
import com.wolfyscript.customcrafting.fabric.inject.RecipeManagerCustomRecipesExt
import com.wolfyscript.customcrafting.resource.ResourceManager
import com.wolfyscript.customcrafting.resource.ResourceManagerCommon
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.Scafall
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import java.io.File

class CustomCraftingFabric(
    val classLoader: ClassLoader,
    val mod: CustomCraftingFabricMod,
    val mcServer: MinecraftServer,
    override val logger: Logger,
) : CustomCraftingCommon(), CustomCraftingBoostrap.CustomCraftingModule {

    val scafall: Scafall = ScafallProvider.get()
    override val bridge: CustomCrafting = this

    override val configurationManager: ConfigurationManager
    override val resourceManager: ResourceManager
    init {
        val configRoot = FabricLoader.getInstance().configDir.toFile()
        val ccDir = File(configRoot, Key.CUSTOMCRAFTING_NAMESPACE)
        configurationManager = ConfigurationManagerImpl(this, ccDir)
        resourceManager = ResourceManagerCommon(this, ccDir)
    }

    override fun onInit() {
        logger.info("Initializing ${CUSTOMCRAFTING_NAMESPACE}...")
    }

    override fun onLoad() {
        logger.info("Loading ${CUSTOMCRAFTING_NAMESPACE}...")
        configurationManager.load()

        resourceManager.resourceLoader.registerListener(recipeManager)
        resourceManager.loadResources()

        (mcServer.recipeManager as RecipeManagerCustomRecipesExt).registerProxyRecipes()
    }

    override fun onEnable() {

    }

    override fun onUnload() {

    }
}