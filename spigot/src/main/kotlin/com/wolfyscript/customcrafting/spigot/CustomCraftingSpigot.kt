package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.resource.DataManager
import com.wolfyscript.customcrafting.resource.DataManagerCommon
import com.wolfyscript.customcrafting.spigot.recipes.AnvilListener
import com.wolfyscript.customcrafting.spigot.recipes.CampfireListener
import com.wolfyscript.customcrafting.spigot.recipes.CauldronListener
import com.wolfyscript.customcrafting.spigot.recipes.CrafterListener
import com.wolfyscript.customcrafting.spigot.recipes.CraftingTableListener
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

    override val dataManager: DataManager = DataManagerCommon(this, bootstrap.dataFolder)

    override fun load() {
        dataManager.loadData()

        registerPlaceholderRecipes(registries.customRecipes.values())
        registerPlaceholderRecipes(registries.customRecipes.values())

        Bukkit.getPluginManager().apply {
            registerEvents(AnvilListener(this@CustomCraftingSpigot), bootstrap)
            registerEvents(CampfireListener(this@CustomCraftingSpigot), bootstrap)
            registerEvents(CauldronListener(this@CustomCraftingSpigot), bootstrap)
            registerEvents(CrafterListener(this@CustomCraftingSpigot), bootstrap)
            registerEvents(CraftingTableListener(this@CustomCraftingSpigot), bootstrap)
            registerEvents(FurnaceListener(this@CustomCraftingSpigot), bootstrap)
            registerEvents(GrindstoneListener(this@CustomCraftingSpigot), bootstrap)
            registerEvents(SmithingListener(this@CustomCraftingSpigot), bootstrap)
            registerEvents(StonecutterListener(this@CustomCraftingSpigot), bootstrap)
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