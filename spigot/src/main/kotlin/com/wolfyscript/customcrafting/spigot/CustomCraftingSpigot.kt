package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.spigot.recipes.crafting.CraftingTableListener
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.slf4j.Logger

class CustomCraftingSpigot(val bootstrap: CustomCraftingSpigotBootstrap, override val logger: Logger) :
    CustomCraftingCommon() {

    override val dataManager: DataManager = DataManagerCommon(this, bootstrap.dataFolder)

    override fun load() {
        dataManager.loadData()



        Bukkit.getPluginManager().apply {
            registerEvents(CraftingTableListener(this@CustomCraftingSpigot), bootstrap)
        }
    }

    override fun unload() {


    }

    companion object {

        val playerCraftingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "crafting_seed")
        val playerSmithingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "smithing_seed")

        val cookingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "cooking_seed")

    }

}