package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface CustomRecipeCooking : CustomRecipe<CustomRecipeCooking> {

    val processingType: ProcessingType

    val source: ItemStack

    val result: ItemStack

    interface ProcessingType {

        val processingTime: Int

        interface Blasting : ProcessingType

        interface Smelting : ProcessingType

        interface Campfire : ProcessingType {
            val soulCampfire: Boolean
            val normalCampfire: Boolean
        }

    }

}