package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.server.CustomCraftingServer
import com.wolfyscript.customcrafting.spigotlike.CustomCraftingServerSpigotLike

class CustomCraftingServerSpigot(
    val spigotLike: CustomCraftingServerSpigotLike,
) : CustomCraftingServer by spigotLike {

    override fun onLoad() {
        spigotLike.onLoad()

    }

    override fun onUnload() {
        spigotLike.onUnload()

    }
}