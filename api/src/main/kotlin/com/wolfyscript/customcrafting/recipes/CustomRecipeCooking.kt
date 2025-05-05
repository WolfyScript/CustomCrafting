package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface CustomRecipeCooking : CustomRecipe<CustomRecipeCooking> {

    val processingType: ProcessingType

    val source: ItemStack

    val result: ItemStack

    @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type")
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonSubTypes(
        JsonSubTypes.Type(value = ProcessingType.Blasting::class, name = "blasting"),
        JsonSubTypes.Type(value = ProcessingType.Smelting::class, name = "smelting"),
        JsonSubTypes.Type(value = ProcessingType.Smoking::class, name = "smoking"),
        JsonSubTypes.Type(value = ProcessingType.Campfire::class, name = "campfire"),
    )
    @JsonPropertyOrder(value = ["type"])
    interface ProcessingType {

        val processingTime: Int

        interface Blasting : ProcessingType

        interface Smoking : ProcessingType

        interface Smelting : ProcessingType

        interface Campfire : ProcessingType {

            /**
             * Weather the recipe can be processed on a soul campfire.
             */
            val soulCampfire: Boolean

            /**
             * Weather the recipe can be processed on a normal campfire.
             */
            val normalCampfire: Boolean
        }

    }

}