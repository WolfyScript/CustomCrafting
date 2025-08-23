package com.wolfyscript.customcrafting.fabric.inject

import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import net.minecraft.server.level.ServerPlayer
import kotlin.random.Random

class RecipesState {

    companion object {

        val crafting = Key.customCrafting("crafting")
        val smithing = Key.customCrafting("smithing")
        val grinding = Key.customCrafting("grinding")
        val repairing = Key.customCrafting("repairing")
        val stonecutting = Key.customCrafting("stonecutting")

    }

    private val seedMap = mutableMapOf<Key, Long>()

    fun getRandom(key: Key): Random = Random(getSeed(key))

    fun getSeed(key: Key): Long = seedMap.getOrPut(key) { Random.nextLong() }

    fun resetSeed(key: Key) = seedMap.set(key, Random.nextLong())

}

fun ServerPlayer.getRecipeRandom(key: Key) : Random {
    return (this as CraftingStatePlayerExt).craftingState.getRandom(key)
}

fun ServerPlayer.getRecipeSeed(key: Key) : Long {
    return (this as CraftingStatePlayerExt).craftingState.getSeed(key)
}

fun ServerPlayer.resetRecipeSeed(key: Key) {
    (this as CraftingStatePlayerExt).craftingState.resetSeed(key)
}