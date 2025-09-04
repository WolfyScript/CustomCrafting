package com.wolfyscript.customcrafting.spigotlike

import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.identifier.Key
import org.bukkit.NamespacedKey

object RecipeSeeds {

    val playerCraftingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "crafting_seed")
    val playerSmithingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "smithing_seed")
    val playerGrindingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "grinding_seed")
    val playerRepairingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "repairing_seed")

    val cookingSeedKey: NamespacedKey = NamespacedKey(Key.CUSTOMCRAFTING_NAMESPACE, "cooking_seed")

}