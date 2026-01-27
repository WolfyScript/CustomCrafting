package com.wolfyscript.customcrafting.spigotlike.recipes

import com.wolfyscript.customcrafting.CustomCrafting
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager

fun PluginManager.registerCommonRecipeListeners(plugin: Plugin, customCrafting: CustomCrafting) {
    registerEvents(AnvilListener(plugin, customCrafting), plugin)
    registerEvents(CampfireListener(customCrafting), plugin)
    registerEvents(CauldronListener(customCrafting), plugin)
    registerEvents(CrafterListener(plugin, customCrafting), plugin)
    registerEvents(CraftingListener(plugin, customCrafting), plugin)
    registerEvents(FurnaceListener(customCrafting), plugin)
    registerEvents(GrindstoneListener(customCrafting), plugin)
    registerEvents(SmithingListener(plugin, customCrafting), plugin)
}