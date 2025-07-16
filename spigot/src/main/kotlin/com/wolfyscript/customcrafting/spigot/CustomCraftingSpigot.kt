package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.resource.DataManager
import com.wolfyscript.customcrafting.resource.DataManagerCommon
import com.wolfyscript.customcrafting.spigot.recipes.*
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.CraftServer
import org.slf4j.Logger

class CustomCraftingSpigot(
    val bootstrap: CustomCraftingSpigotBootstrap,
    override val logger: Logger,
) :
    CustomCraftingCommon() {

    override val configurationManager: ConfigurationManager =
        ConfigurationManagerImpl(this, bootstrap.plugin.dataFolder)
    override val dataManager: DataManager = DataManagerCommon(this, bootstrap.plugin.dataFolder)

    override fun load() {
        configurationManager.load()

        dataManager.resourceLoader.registerListener(recipeManager)
        dataManager.loadData()

        registerPlaceholderRecipes(recipeManager.index.values())
        registerDisplayRecipes(recipeManager.index.values())
    }

    override fun enabled() {
        commands.registerCommands(ScafallProvider.get().server.minecraftServer.commands.dispatcher)

        Bukkit.getPluginManager().apply {
            registerEvents(AnvilListener(this@CustomCraftingSpigot), bootstrap.plugin)
            registerEvents(CampfireListener(this@CustomCraftingSpigot), bootstrap.plugin)
            registerEvents(CauldronListener(this@CustomCraftingSpigot), bootstrap.plugin)
            registerEvents(CrafterListener(this@CustomCraftingSpigot), bootstrap.plugin)
            registerEvents(CraftingListener(this@CustomCraftingSpigot), bootstrap.plugin)
            registerEvents(FurnaceListener(this@CustomCraftingSpigot), bootstrap.plugin)
            registerEvents(GrindstoneListener(this@CustomCraftingSpigot), bootstrap.plugin)
            registerEvents(SmithingListener(this@CustomCraftingSpigot), bootstrap.plugin)
            registerEvents(StonecutterListener(this@CustomCraftingSpigot), bootstrap.plugin)
        }
    }

    override fun unload() {


    }

    private fun getCommandMap(): CommandMap {
        return (Bukkit.getServer() as CraftServer).commandMap
    }


    companion object {

        val playerCraftingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "crafting_seed")
        val playerSmithingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "smithing_seed")
        val playerGrindingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "grinding_seed")
        val playerRepairingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "repairing_seed")

        val cookingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "cooking_seed")

    }

}