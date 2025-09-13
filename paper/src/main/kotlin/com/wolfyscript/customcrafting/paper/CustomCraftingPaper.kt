package com.wolfyscript.customcrafting.paper

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingBoostrap
import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.core.commands.CCCommands
import com.wolfyscript.customcrafting.paper.recipes.StonecutterListener
import com.wolfyscript.customcrafting.resource.ResourceManager
import com.wolfyscript.customcrafting.resource.ResourceManagerCommon
import com.wolfyscript.customcrafting.spigotlike.recipes.registerCommonRecipeListeners
import com.wolfyscript.customcrafting.spigotlike.recipes.registerDisplayRecipes
import com.wolfyscript.customcrafting.spigotlike.recipes.registerPlaceholderRecipes
import com.wolfyscript.scafall.ScafallProvider
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.slf4j.Logger

class CustomCraftingPaper(val plugin: Plugin, override val logger: Logger) : CustomCraftingCommon(), CustomCraftingBoostrap.CustomCraftingModule {

    override val configurationManager: ConfigurationManager =
        ConfigurationManagerImpl(this, plugin.dataFolder)
    override val resourceManager: ResourceManager = ResourceManagerCommon(this, plugin.dataFolder)

    override val bridge: CustomCrafting = this

    override fun onLoad() {
        configurationManager.load()

        resourceManager.resourceLoader.registerListener(recipeManager)
        resourceManager.loadResources()

        registerPlaceholderRecipes(recipeManager.index.values())
        registerDisplayRecipes(recipeManager.index.values())
    }

    override fun onEnable() {
        CCCommands.registerCommands(ScafallProvider.get().server.minecraftServer.commands.dispatcher)

        Bukkit.getPluginManager().apply {
            registerCommonRecipeListeners(plugin, this@CustomCraftingPaper)
            registerEvents(StonecutterListener(this@CustomCraftingPaper), plugin)
        }
    }

    override fun onUnload() {
    }

}