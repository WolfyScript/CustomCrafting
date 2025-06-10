package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.configuration.ConfigurationManagerImpl
import com.wolfyscript.customcrafting.resource.DataManager
import com.wolfyscript.customcrafting.resource.DataManagerCommon
import com.wolfyscript.customcrafting.spigot.recipes.AnvilListener
import com.wolfyscript.customcrafting.spigot.recipes.CampfireListener
import com.wolfyscript.customcrafting.spigot.recipes.CauldronListener
import com.wolfyscript.customcrafting.spigot.recipes.CrafterListener
import com.wolfyscript.customcrafting.spigot.recipes.CraftingListener
import com.wolfyscript.customcrafting.spigot.recipes.FurnaceListener
import com.wolfyscript.customcrafting.spigot.recipes.GrindstoneListener
import com.wolfyscript.customcrafting.spigot.recipes.SmithingListener
import com.wolfyscript.customcrafting.spigot.recipes.StonecutterListener
import com.wolfyscript.customcrafting.spigot.recipes.registerPlaceholderRecipes
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.slf4j.Logger

class CustomCraftingSpigot(
    val bootstrap: CustomCraftingSpigotBootstrap,
    override val logger: Logger,
) :
    CustomCraftingCommon() {

    override val configurationManager: ConfigurationManager = ConfigurationManagerImpl(this, bootstrap.plugin.dataFolder)
    override val dataManager: DataManager = DataManagerCommon(this, bootstrap.plugin.dataFolder)

    override fun load() {
        configurationManager.load()
        dataManager.loadData()

        registerPlaceholderRecipes(registries.customRecipes.values())
        registerPlaceholderRecipes(registries.customRecipes.values())
    }

    override fun enabled() {
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


    companion object {

        val playerCraftingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "crafting_seed")
        val playerSmithingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "smithing_seed")
        val playerGrindingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "grinding_seed")
        val playerRepairingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "repairing_seed")

        val cookingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "cooking_seed")

    }

}