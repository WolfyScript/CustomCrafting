package com.wolfyscript.customcrafting.spigotlike.recipes

import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.configuration.mechanics.CauldronSettings
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class CauldronListener(val customCrafting: CustomCrafting) : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) {
            return
        }
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        if (event.item != null && event.item!!.type != Material.AIR) {
            return // Only allow interactions with empty hand
        }
        val block = event.clickedBlock ?: return
        if (block.type != Material.CAULDRON && block.type != Material.WATER_CAULDRON && block.type != Material.LAVA_CAULDRON) {
            return
        }

        val settings = customCrafting.configurationManager.gameMechanicSettings.cauldron
        val allowedInteraction = when (settings.interactionType) {
            CauldronSettings.InteractionType.SNEAKING -> {
                event.player.isSneaking
            }
            CauldronSettings.InteractionType.DEFAULT -> {
                !event.player.isSneaking
            }
        }
        if (!allowedInteraction) {
            return
        }

        // TODO: cauldron GUI
    }

}