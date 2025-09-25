package com.wolfyscript.customcrafting.util

import java.util.Properties

object CustomCraftingProperties {

    val properties: Properties = Properties()

    init {
        properties.load(javaClass.classLoader.getResourceAsStream("com/wolfyscript/customcrafting/vals.properties"))
    }

    val release: String = properties.getProperty("release") ?: "unknown"

}