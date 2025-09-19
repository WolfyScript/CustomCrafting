package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.core.commands.CCCommands
import com.wolfyscript.customcrafting.resource.ResourceManager
import com.wolfyscript.customcrafting.resource.ResourceManagerCommon
import com.wolfyscript.customcrafting.spigotlike.recipes.registerCommonRecipeListeners
import com.wolfyscript.customcrafting.spigotlike.recipes.registerDisplayRecipes
import com.wolfyscript.customcrafting.spigotlike.recipes.registerPlaceholderRecipes
import com.wolfyscript.scafall.ScafallProvider
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger

class CustomCraftingSpigot(
    val plugin: JavaPlugin,
    override val logger: Logger,
) :
    CustomCraftingCommon(), CustomCraftingBoostrap.CustomCraftingModule {

    override val bridge: CustomCrafting = this

    override val configurationManager: ConfigurationManager =
        ConfigurationManagerImpl(this, plugin.dataFolder)
    override val resourceManager: ResourceManager = ResourceManagerCommon(this, plugin.dataFolder)

    override fun onLoad() {
        configurationManager.load()

        resourceManager.resourceLoader.registerListener(recipeManager)

    }

    override fun onEnable() {
        ScafallProvider.get().dependencyManager.onAllDependenciesInitialized {
            resourceManager.loadResources()
            registerPlaceholderRecipes(recipeManager.recipes())
            registerDisplayRecipes(recipeManager.recipes())
        }

        CCCommands.registerCommands(ScafallProvider.get().server.minecraftServer.commands.dispatcher)

        Bukkit.getPluginManager().registerCommonRecipeListeners(plugin, this)
    }

    override fun onUnload() {

    }

}