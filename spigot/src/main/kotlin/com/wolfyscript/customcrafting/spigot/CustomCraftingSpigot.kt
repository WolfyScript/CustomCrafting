package com.wolfyscript.customcrafting.spigot

import com.wolfyscript.customcrafting.CustomCraftingCommon
import org.bukkit.NamespacedKey
import org.slf4j.Logger

class CustomCraftingSpigot(val bootstrap: CustomCraftingSpigotBootstrap, override val logger: Logger) : CustomCraftingCommon() {

    companion object {

        val playerCraftingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "crafting_seed")
        val playerSmithingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "smithing_seed")

        val cookingSeedKey: NamespacedKey = NamespacedKey("customcrafting", "cooking_seed")

    }

}