package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingBoostrap
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
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger

class CustomCraftingSpigot(
    val classLoader: ClassLoader,
    val plugin: JavaPlugin,
    override val logger: Logger,
) :
    CustomCraftingCommon(), CustomCraftingBoostrap.CustomCraftingModule {

    override val bridge: CustomCrafting = this

    override val configurationManager: ConfigurationManager =
        ConfigurationManagerImpl(this, plugin.dataFolder)
    override val dataManager: DataManager = DataManagerCommon(this, plugin.dataFolder)

    override fun onLoad() {
        configurationManager.load()

        dataManager.resourceLoader.registerListener(recipeManager)
        dataManager.loadData()

        registerPlaceholderRecipes(recipeManager.index.values())
        registerDisplayRecipes(recipeManager.index.values())
    }

    override fun onEnable() {
        commands.registerCommands(ScafallProvider.get().server.minecraftServer.commands.dispatcher)

        Bukkit.getPluginManager().apply {
            registerEvents(AnvilListener(this@CustomCraftingSpigot), plugin)
            registerEvents(CampfireListener(this@CustomCraftingSpigot), plugin)
            registerEvents(CauldronListener(this@CustomCraftingSpigot), plugin)
            registerEvents(CrafterListener(this@CustomCraftingSpigot), plugin)
            registerEvents(CraftingListener(this@CustomCraftingSpigot), plugin)
            registerEvents(FurnaceListener(this@CustomCraftingSpigot), plugin)
            registerEvents(GrindstoneListener(this@CustomCraftingSpigot), plugin)
            registerEvents(SmithingListener(this@CustomCraftingSpigot), plugin)
            registerEvents(StonecutterListener(this@CustomCraftingSpigot), plugin)
        }
    }

    override fun onUnload() {

    }

    companion object {

        val playerCraftingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "crafting_seed")
        val playerSmithingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "smithing_seed")
        val playerGrindingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "grinding_seed")
        val playerRepairingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "repairing_seed")

        val cookingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "cooking_seed")

    }

}