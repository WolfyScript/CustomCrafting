package com.wolfyscript.customcrafting.paper

import com.wolfyscript.customcrafting.paper.recipes.StonecutterListener
import com.wolfyscript.customcrafting.server.CustomCraftingServer
import com.wolfyscript.customcrafting.spigotlike.CustomCraftingServerSpigotLike
import org.bukkit.Bukkit

class CustomCraftingServerPaper(val spigotLike: CustomCraftingServerSpigotLike) :
    CustomCraftingServer by spigotLike {

    override fun onLoad() {
        spigotLike.onLoad()

        Bukkit.getPluginManager().apply {
            registerEvents(StonecutterListener(spigotLike.customCrafting), spigotLike.plugin)
        }
    }

}